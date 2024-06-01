package org.app.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zfq
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    Long id;

    String name;

    String password;
}
