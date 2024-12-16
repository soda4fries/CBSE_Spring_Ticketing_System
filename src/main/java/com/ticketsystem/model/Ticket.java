package com.ticketsystem.model;

import java.util.Date;
import java.util.List;

public class Ticket {
    private String id;
    private String title;
    private String description;
    private String status;
    private String assignedTo;
    private List<Reply> replies;
    private Date createdAt;
    private Date lastUpdatedAt;
    private Date resolvedAt;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }

    public List<Reply> getReplies() { return replies; }
    public void setReplies(List<Reply> replies) { this.replies = replies; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getLastUpdatedAt() { return lastUpdatedAt; }
    public void setLastUpdatedAt(Date lastUpdatedAt) { this.lastUpdatedAt = lastUpdatedAt; }

    public Date getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Date resolvedAt) { this.resolvedAt = resolvedAt; }
}
