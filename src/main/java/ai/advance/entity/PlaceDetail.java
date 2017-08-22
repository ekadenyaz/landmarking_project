package ai.advance.entity;

import lombok.Data;

/**
 * Created by ekadenyaz on 8/10/17.
 */
@Data
public class PlaceDetail {
    private GeometryLocation geometryLocation;
    private String placeName;
    private String zipCode;
}
