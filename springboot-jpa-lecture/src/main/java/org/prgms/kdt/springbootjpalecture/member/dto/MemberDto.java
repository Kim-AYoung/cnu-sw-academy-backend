package org.prgms.kdt.springbootjpalecture.member.dto;

import lombok.*;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    private Long id;
    private String name;
    private String nickName;
    private int age;
    private String address;
    private String description;
}
