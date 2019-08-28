package org.dxworks.gitsecond;

import lombok.*;
import org.eclipse.jgit.diff.DiffEntry;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class ChangeData {

    private String commitID;
    private String oldFileName;
    private String newFileName;
    private DiffEntry.ChangeType type;
    private String diff;

    private int addedLines;
    private int deletedLines;
}
