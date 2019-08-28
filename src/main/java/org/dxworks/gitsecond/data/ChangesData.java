package org.dxworks.gitsecond.data;

import lombok.*;

import java.util.List;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class ChangesData {
    private String otherCommitId;

    private List<ChangeData> changes;
}
