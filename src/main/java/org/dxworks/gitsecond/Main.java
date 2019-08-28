package org.dxworks.gitsecond;

import org.dxworks.gitsecond.data.ChangeData;
import org.dxworks.gitsecond.data.CommitData;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static final String REPO_NAME = "gitTest";

    public static void main(String[] args) {
        GitClient gitClient = new GitClient();

        try {
            gitClient.cloneAndInitializeRepository("https://github.com/nagyDarius/gitLogTest.git", REPO_NAME);

        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        List<CommitData> commitDatas = gitClient.generateGitLogForDx(REPO_NAME);
        List<CommitData> myCommits = new ArrayList<>();

        System.out.println(commitDatas);

        final Map<String, List<String>> files = new HashMap<>();

        List<ChangeData> changes = commitDatas.stream()
                .filter(commitData -> !commitData.isMergeCommit())
                .flatMap(commitData -> commitData.getChangeSets().stream())
                .flatMap(changeSet -> changeSet.getChanges().stream())
                .collect(Collectors.toList());
        Collections.reverse(changes);

        changes.forEach(change -> {
            String fileName = change.getNewFileName();
            files.computeIfAbsent(fileName, k -> new ArrayList<>());
            List<String> lines = Arrays.asList(change.getDiff().split("\n"));
            lines.stream().filter(line -> line.startsWith("@@")).forEach(line -> {
                int contentLineIndex = lines.indexOf(line) + 1;
                line = line.replaceAll("@", "").trim();
                String[] info = line.split(" ");
                String removeInfo = info[0].substring(1);
                String addInfo = info[1].substring(1);
                String[] removeNumbers = removeInfo.split(",");
                String[] addNumbers = addInfo.split(",");
                int removeStart = Integer.parseInt(removeNumbers[0]) - 1;
                int removeSize;
                if (removeNumbers.length < 2)
                    removeSize = 1;
                else
                    removeSize = Integer.parseInt(removeNumbers[1]);

                int addStart = Integer.parseInt(addNumbers[0]) - 1;
                int addSize;
                if (addNumbers.length < 2)
                    addSize = 1;
                else
                    addSize = Integer.parseInt(addNumbers[1]);
                List<String> fileContent = files.get(fileName);
                if (fileContent.size() != 0) {
                    for (int i = removeStart; i < removeStart + removeSize; i++) {
                        fileContent.set(i, fileContent.get(i) + " -(" + lines.get(contentLineIndex + i - removeStart) + ") by " + getAuthorName(commitDatas, change));
//                        fileContent.remove(removeStart);
                    }
                }
                for (int i = addStart; i < addStart + addSize; i++) {
                    if (i < fileContent.size())
                        fileContent.add(i, lines.get(contentLineIndex + i - addStart) + "         by " + getAuthorName(commitDatas, change));
                    else
                        fileContent.add(lines.get(contentLineIndex + i - addStart) + "          by " + getAuthorName(commitDatas, change));
                }
            });
        });

        files.entrySet().forEach(entry -> entry.getValue().forEach(System.out::println));

    }

    private static String getAuthorName(List<CommitData> commitDatas, ChangeData change) {
        return commitDatas.stream().filter(commit -> commit.getId().equals(change.getCommitID())).findFirst().get().getAuthorName();
    }
}
