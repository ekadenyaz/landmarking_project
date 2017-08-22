package ai.advance.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by ekadenyaz on 8/11/17.
 */
@Data
public class GeometryLocation implements Serializable {
    private String latitude;
    private String longitude;
}
