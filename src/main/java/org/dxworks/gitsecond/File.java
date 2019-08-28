package org.dxworks.gitsecond;

import lombok.*;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@ToString
public class File {
    private String name;
}
