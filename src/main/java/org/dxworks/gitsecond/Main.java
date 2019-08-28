package org.dxworks.gitsecond;

import org.eclipse.jgit.api.errors.GitAPIException;

import java.util.List;

public class Main {

    public static final String STUDENT_MANAGER = "studentManager";

    public static void main(String[] args) {
        GitClient gitClient = new GitClient();

        try {
            gitClient.cloneAndInitializeRepository("https://github.com/MarioRivis/studentManager", STUDENT_MANAGER);
            List<CommitData> commitDatas = gitClient.generateGitLogForDx(STUDENT_MANAGER);

            System.out.println(commitDatas);
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

    }
}
