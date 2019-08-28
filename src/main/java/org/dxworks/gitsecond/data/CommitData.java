package org.dxworks.gitsecond.data;

import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class CommitData {

    private String id;
    private String message;
    private String authorName;
    private String authorEmail;
    private Date date;

    private List<String> parentIds;

    private List<ChangesData> changeSets;

    public boolean isMergeCommit() {
        return parentIds != null && parentIds.size() >= 2;
    }
}
