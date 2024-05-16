package org.app.entity;

import lombok.Data;
import org.graalvm.collections.Pair;

@Data
public class WorkStatus {
    private WorkMode workmode;

    private Pair<Integer, Integer> range;
}
