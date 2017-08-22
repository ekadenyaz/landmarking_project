package ai.advance.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ekadenyaz on 8/11/17.
 */
@Data
public class GeometryLocation implements Serializable {
    private Location location;
    private Location northeastViewport;
    private Location southwestViewport;

}
