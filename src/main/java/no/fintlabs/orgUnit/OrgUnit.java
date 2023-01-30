package no.fintlabs.orgUnit;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrgUnit {
    private Long id;
    private String organisationUnitId;
    private String name;
    private String shortName;
    private String parentRef;
    private String childrenRef;
    private String managerRef;

}
