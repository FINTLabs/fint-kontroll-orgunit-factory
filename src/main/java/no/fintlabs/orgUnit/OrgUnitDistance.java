package no.fintlabs.orgUnit;


import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class OrgUnitDistance {
    private String key;
    private String orgUnitId;
    private String subOrgUnitId;
    private int distance;
}
