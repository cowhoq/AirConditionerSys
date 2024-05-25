package org.app.entity;

import lombok.Data;
import lombok.Getter;
import org.graalvm.collections.Pair;

@Data
@Getter
public class WorkStatus {
    private WorkMode workmode;

    private Pair<Integer, Integer> range;
}
