package zool.rabbitmq.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : zoolye
 * @date : 2019-01-14 13:53
 * @describe :
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Packaged {

    private String id;

    private String name;

    private String description;

}
