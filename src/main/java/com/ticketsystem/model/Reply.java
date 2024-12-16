package com.ticketsystem.model;

import java.util.Date;
import java.util.List;

public class Reply {
    private String id;
    private String content;
    private String parentId;
    private Date timestamp;
    private Date lastEditedAt;
    private List<Reply> children;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public Date getLastEditedAt() { return lastEditedAt; }
    public void setLastEditedAt(Date lastEditedAt) { this.lastEditedAt = lastEditedAt; }

    public List<Reply> getChildren() { return children; }
    public void setChildren(List<Reply> children) { this.children = children; }
}