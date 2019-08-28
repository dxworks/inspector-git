package org.dxworks.gitsecond;

import org.dxworks.gitsecond.data.ChangeData;
import org.dxworks.gitsecond.data.ChangesData;
import org.dxworks.gitsecond.data.CommitData;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GitClient {

    public static final String REPOS_PATH = Constants.APP_FOLDER_PATH + File.separator + "repos";
    private static final Logger log = LoggerFactory.getLogger(GitClient.class);
    private final String could_not_parse_changes_correctly = "Could not parse changes correctly";

    public GitClient() {
        initializeReposPath();
    }


    private void initializeReposPath() {
        File repo_file_path = new File(REPOS_PATH);
        if (!repo_file_path.exists()) {
            boolean didMakeDirs = repo_file_path.mkdirs();
            if (!didMakeDirs) {
                log.error("Could not make directory " + REPOS_PATH);
            }
        }

        log.info("Repos path set to " + REPOS_PATH);
    }

    private Git getRepositoryByProjectIdAndRepoName(String repoName) throws IOException {
        return Git.open(Paths.get(REPOS_PATH + File.separator + repoName + File.separator + ".git").toFile());
    }

    public void checkoutRevisionForRepo(String revisionName, String repoName) {
        try {
            Git git = this.getRepositoryByProjectIdAndRepoName(repoName);
            git.checkout().setName(revisionName).call();
        } catch (IOException ex) {
            log.error("could not find repository for " + repoName, ex);
        } catch (GitAPIException ex) {
            log.error("Error trying to checkout revision " + revisionName + " for student " + repoName, ex);
        }

    }

    public void cloneAndInitializeRepository(String repositoryURL, String repoName) throws GitAPIException {

        File studentFolder = Paths.get(REPOS_PATH + File.separator + repoName).toFile();
        if (studentFolder.exists() && studentFolder.isDirectory()) {
            log.error("Student " + repoName + " already has a cloned repository");
            return;
        } else {
            boolean createdStudentDirectory = studentFolder.mkdirs();
            if (!createdStudentDirectory) {
                log.error("Could not create directory for student " + repoName);
                return;
            }
        }

        CloneCommand cloneCommand = Git.cloneRepository().setURI(repositoryURL).setDirectory(studentFolder).setCloneAllBranches(true).setBranch("master").setCredentialsProvider(new UsernamePasswordCredentialsProvider("mario.rivis@gmail.com", "just6and9"));
        cloneCommand.call();
    }

    public String getFileContentForRepoAndRevision(String filePath, String repoName, String revisionName) throws IOException {
        Repository repository = this.getRepositoryByProjectIdAndRepoName(repoName).getRepository();
        ObjectId lastCommitId = repository.resolve(revisionName);
        RevWalk revWalk = new RevWalk(repository);
        RevCommit commit = revWalk.parseCommit(lastCommitId);
        RevTree tree = commit.getTree();
        TreeWalk treeWalk = new TreeWalk(repository);
        treeWalk.addTree(tree);
        treeWalk.setRecursive(true);
        treeWalk.setFilter(PathFilter.create(filePath));
        if (!treeWalk.next()) {
            System.out.println("File not found");
            throw new FileNotFoundException(filePath);
        } else {
            ObjectId objectId = treeWalk.getObjectId(0);
            ObjectLoader loader = repository.open(objectId);
            return new String(loader.getBytes());
        }
    }

    public List<CommitData> generateGitLogForDx(String repoName) {
        try {
            Git git = this.getRepositoryByProjectIdAndRepoName(repoName);
            Repository repository = git.getRepository();
            return StreamSupport.stream(git.log().setRevFilter(RevFilter.ALL).call().spliterator(), false)
                    .map((revCommit) -> this.getCommitDetails(repository, revCommit))
                    .collect(Collectors.toList());
        } catch (GitAPIException var4) {
            log.error("Git Api error", var4);
        } catch (IOException var5) {
            log.error("could not find repository " + repoName, var5);
        }

        return null;
    }

    private CommitData getCommitDetails(Repository repository, RevCommit revCommit) {
        return CommitData.builder()
                .id(revCommit.getName())
                .authorName(revCommit.getAuthorIdent().getName())
                .authorEmail(revCommit.getAuthorIdent().getEmailAddress())
                .date(new Date((long) revCommit.getCommitTime() * 1000L))
                .message(revCommit.getFullMessage())
                .changeSets(this.getCommitChanges(repository, revCommit))
                .parentIds(Arrays.stream(revCommit.getParents()).map(RevCommit::getName).collect(Collectors.toList()))
                .build();
    }

    private List<ChangesData> getCommitChanges(Repository repository, RevCommit revCommit) {
        ObjectReader reader = repository.newObjectReader();
        CanonicalTreeParser currentCommitTreeIterator = new CanonicalTreeParser();

        try {
            currentCommitTreeIterator.reset(reader, revCommit.getTree().getId());

            if (revCommit.getParentCount() == 0) {
                AbstractTreeIterator parentTreeIterator = new EmptyTreeIterator();
                List<DiffEntry> diffs = getDiffsBetweenCommits(repository, parentTreeIterator, currentCommitTreeIterator);
                List<ChangeData> changes = transformDiffsToChangeDatas(revCommit, repository, diffs, 0);
                return Collections.singletonList(new ChangesData(null, changes));
            } else {
                List<ChangesData> changesData = Arrays.stream(revCommit.getParents())
                        .map(parentCommit -> getChangesData(repository, reader, currentCommitTreeIterator, revCommit, parentCommit, revCommit.getParentCount() >= 2 ? Integer.MAX_VALUE : 0))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                if (changesData.size() != revCommit.getParentCount()) {
                    log.warn("Not all merge commit parents have been correctly parsed!");
                }
                return changesData;
            }

        } catch (IOException e) {
            log.error(could_not_parse_changes_correctly, e);
            return Collections.emptyList();
        }
    }

    private ChangesData getChangesData(Repository repository, ObjectReader reader, CanonicalTreeParser currentCommitTreeIterator, RevCommit revCommit, RevCommit parentCommit, int contextLines) {
        CanonicalTreeParser parentIterator = new CanonicalTreeParser();
        try {
            parentIterator.reset(reader, parentCommit.getTree().getId());
            List<DiffEntry> diffsBetweenCommits = getDiffsBetweenCommits(repository, parentIterator, currentCommitTreeIterator);
            List<ChangeData> changeDatas = transformDiffsToChangeDatas(revCommit, repository, diffsBetweenCommits, contextLines);

            if (changeDatas != null) {
                return new ChangesData(parentCommit.getName(), changeDatas);
            } else {
                return null;
            }
        } catch (IOException e) {
            log.error(could_not_parse_changes_correctly, e);
            return null;
        } finally {
            try {
                currentCommitTreeIterator.reset(reader, revCommit.getTree().getId());
            } catch (IOException e) {
                log.error(could_not_parse_changes_correctly, e);
            }
        }
    }

    private List<ChangeData> transformDiffsToChangeDatas(RevCommit revCommit, Repository repository, List<DiffEntry> diffs, int contextLines) {
        return diffs.stream().map(diff -> {
            ChangeData changeData = ChangeData.builder()
                    .type(diff.getChangeType())
                    .oldFileName(diff.getOldPath())
                    .commitID(revCommit.getName())
                    .newFileName(diff.getNewPath().equals("/dev/null") ? diff.getOldPath() : diff.getNewPath())
                    .build();
            setNoOfLinesDeletedAndAdded(repository, diff, changeData, contextLines);
            return changeData;
        }).collect(Collectors.toList());
    }

    private List<DiffEntry> getDiffsBetweenCommits(Repository repository, AbstractTreeIterator parentTreeIterator, AbstractTreeIterator currentCommitTreeIterator) throws IOException {
        DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
        df.setRepository(repository);
        df.setDiffComparator(RawTextComparator.DEFAULT);
        df.setDetectRenames(true);
        return df.scan(parentTreeIterator, currentCommitTreeIterator);
    }

    private void setNoOfLinesDeletedAndAdded(Repository repository, DiffEntry diff, ChangeData repoChangeBlock, int contextLines) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DiffFormatter df = new DiffFormatter(out);
        df.setRepository(repository);

        try {
            df.setContext(contextLines);
            df.setDetectRenames(true);
            df.format(diff);
            RawText r = new RawText(out.toByteArray());
            r.getLineDelimiter();
            String modifications = out.toString();
            out.reset();
            countAddedAndDeletedLines(modifications, repoChangeBlock);
        } catch (IOException e) {
            log.error("Diff between commits could not be parsed correctly!", e);
        }

    }

    private void countAddedAndDeletedLines(String modifications, ChangeData repoChangeBlock) {
        int addedLines = 0;
        int deletedLines = 0;
        List<String> lines = Arrays.asList(modifications.split("\n"));
        lines = this.trimList(lines);

        for (String line : lines) {
            if (line.startsWith("+")) {
                ++addedLines;
            }

            if (line.startsWith("-")) {
                ++deletedLines;
            }
        }

        repoChangeBlock.setDiff(modifications);
        repoChangeBlock.setAddedLines(addedLines);
        repoChangeBlock.setDeletedLines(deletedLines);
    }

    private List<String> trimList(List<String> lines) {
        for (int i = 0; i < lines.size(); ++i) {
            if (lines.get(i).startsWith("@@")) {
                return lines.subList(i, lines.size());
            }
        }

        return Collections.emptyList();
    }

}