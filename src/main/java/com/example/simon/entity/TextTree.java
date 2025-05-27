package com.example.simon.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TextTree extends BaseEntity {
    private Integer treeId;
    private String module; // writing, reading, dialogue
    private Integer refId;
    private String structureJson; // 树结构本体（挂载文本分段/分句/分词）
}