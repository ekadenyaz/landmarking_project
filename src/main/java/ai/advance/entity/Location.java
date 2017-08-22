package ai.advance.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by ekadenyaz on 8/22/17.
 */
@Data
public class Location implements Serializable {
    private String latitude;
    private String longitude;
}
