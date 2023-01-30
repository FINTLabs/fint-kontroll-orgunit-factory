package no.fintlabs.orgUnit;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrgUnit {
    private Long id;
    private String organisationUnitId;
    private String name;
    private String shortName;
    private String parentRef;
    private List<String> childrenRef;
    private String managerRef;

}
