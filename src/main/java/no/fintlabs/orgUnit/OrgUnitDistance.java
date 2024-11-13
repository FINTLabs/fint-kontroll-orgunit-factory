package no.fintlabs.orgUnit;


import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrgUnitDistance that = (OrgUnitDistance) o;
        return distance == that.distance && Objects.equals(key, that.key) && Objects.equals(orgUnitId, that.orgUnitId) && Objects.equals(subOrgUnitId, that.subOrgUnitId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, orgUnitId, subOrgUnitId, distance);
    }
}
