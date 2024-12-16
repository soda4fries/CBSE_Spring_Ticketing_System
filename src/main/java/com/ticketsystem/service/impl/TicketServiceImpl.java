package com.ticketsystem.service.impl;

import com.ticketsystem.model.Ticket;
import com.ticketsystem.model.Reply;
import com.ticketsystem.model.User;
import com.ticketsystem.service.TicketService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class TicketServiceImpl implements TicketService {
    private final Map<String, Ticket> tickets = new ConcurrentHashMap<>();
    private final Map<String, List<Reply>> replies = new ConcurrentHashMap<>();
    private final Map<String, User> users = new ConcurrentHashMap<>();

    @Override
    public Ticket createTicket(String title, String description) {
        Ticket ticket = new Ticket();
        ticket.setId(UUID.randomUUID().toString());
        ticket.setTitle(title != null ? title : "");
        ticket.setDescription(description != null ? description : "");
        ticket.setStatus("OPEN");  // Ensure status is never null
        
        Date now = new Date();
        ticket.setCreatedAt(now);
        ticket.setLastUpdatedAt(now);
        
        ticket.setReplies(new ArrayList<>());

        tickets.put(ticket.getId(), ticket);
        replies.put(ticket.getId(), new ArrayList<>());

        return ticket;
    }

    @Override
    public Ticket getTicket(String id) {
        Ticket ticket = tickets.get(id);
        if (ticket != null) {
            List<Reply> ticketReplies = replies.get(id);
            if (ticketReplies != null) {
                ticket.setReplies(ticketReplies);
            }
        }
        return ticket;
    }

    @Override
    public void updateTicket(Ticket ticket) {
        if (!tickets.containsKey(ticket.getId())) {
            throw new IllegalArgumentException("Ticket not found: " + ticket.getId());
        }
        
        // Ensure required fields aren't null
        if (ticket.getStatus() == null) {
            ticket.setStatus("OPEN");
        }
        if (ticket.getCreatedAt() == null) {
            ticket.setCreatedAt(new Date());
        }
        
        ticket.setLastUpdatedAt(new Date());
        tickets.put(ticket.getId(), ticket);
    }

    @Override
    public void assignTicket(String ticketId, String userId) {
        Ticket ticket = tickets.get(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket not found: " + ticketId);
        }
        ticket.setAssignedTo(userId);
        ticket.setLastUpdatedAt(new Date());
        tickets.put(ticketId, ticket);
    }

    @Override
    public Reply addReply(String ticketId, String content, String parentReplyId) {
        List<Reply> ticketReplies = replies.get(ticketId);
        if (ticketReplies == null) {
            throw new IllegalArgumentException("Ticket not found: " + ticketId);
        }

        Reply reply = new Reply();
        reply.setId(UUID.randomUUID().toString());
        reply.setContent(content);
        reply.setParentId(parentReplyId);
        reply.setTimestamp(new Date());
        reply.setChildren(new ArrayList<>());

        ticketReplies.add(reply);

        // Update ticket last updated timestamp
        Ticket ticket = tickets.get(ticketId);
        ticket.setLastUpdatedAt(new Date());
        tickets.put(ticketId, ticket);

        return reply;
    }

    @Override
    public void editReply(String ticketId, String replyId, String newContent) {
        List<Reply> ticketReplies = replies.get(ticketId);
        if (ticketReplies == null) {
            throw new IllegalArgumentException("Ticket not found: " + ticketId);
        }

        for (Reply reply : ticketReplies) {
            if (reply.getId().equals(replyId)) {
                reply.setContent(newContent);
                reply.setLastEditedAt(new Date());
                return;
            }
        }
        throw new IllegalArgumentException("Reply not found: " + replyId);
    }

    @Override
    public void resolveTicket(String ticketId) {
        Ticket ticket = tickets.get(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket not found: " + ticketId);
        }
        ticket.setStatus("RESOLVED");
        ticket.setResolvedAt(new Date());
        ticket.setLastUpdatedAt(new Date());
        tickets.put(ticketId, ticket);
    }

    @Override
    public List<Ticket> getAllTickets() {
        return new ArrayList<>(tickets.values());
    }

    @Override
    public List<Ticket> getTicketsByStatus(String status) {
        return tickets.values().stream()
                .filter(ticket -> status.equals(ticket.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Ticket> getTicketsByAssignee(String userId) {
        return tickets.values().stream()
                .filter(ticket -> userId.equals(ticket.getAssignedTo()))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, List<Ticket>> getTicketsByDepartment() {
        return tickets.values().stream()
                .filter(ticket -> ticket.getAssignedTo() != null)
                .collect(Collectors.groupingBy(
                        ticket -> ticket.getAssignedTo().split("\\.")[0],
                        Collectors.toList()
                ));
    }

    @Override
    public List<Ticket> searchTickets(String searchTerm) {
        String term = searchTerm.toLowerCase();
        return tickets.values().stream()
                .filter(ticket ->
                        ticket.getTitle().toLowerCase().contains(term) ||
                                ticket.getDescription().toLowerCase().contains(term))
                .collect(Collectors.toList());
    }

    @Override
    public List<Reply> getTicketRepliesTree(String ticketId) {
        List<Reply> allReplies = replies.get(ticketId);
        if (allReplies == null) {
            return new ArrayList<>();
        }

        Map<String, List<Reply>> replyChildren = new HashMap<>();
        List<Reply> rootReplies = new ArrayList<>();

        for (Reply reply : allReplies) {
            if (reply.getParentId() == null) {
                rootReplies.add(reply);
            } else {
                replyChildren.computeIfAbsent(reply.getParentId(), k -> new ArrayList<>())
                        .add(reply);
            }
        }

        return buildReplyTree(rootReplies, replyChildren);
    }

    private List<Reply> buildReplyTree(List<Reply> replies, Map<String, List<Reply>> children) {
        for (Reply reply : replies) {
            List<Reply> childReplies = children.get(reply.getId());
            if (childReplies != null) {
                childReplies.sort(Comparator.comparing(Reply::getTimestamp));
                reply.setChildren(buildReplyTree(childReplies, children));
            }
        }
        replies.sort(Comparator.comparing(Reply::getTimestamp));
        return replies;
    }

    @Override
    public Map<String, Integer> getTicketStatistics() {
        return tickets.values().stream()
            .filter(ticket -> ticket.getStatus() != null)  
            .collect(Collectors.groupingBy(
                ticket -> ticket.getStatus(),
                Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
            ));
    }

    @Override
    public List<Ticket> getRecentTickets(int limit) {
        return tickets.values().stream()
                .sorted(Comparator.comparing(Ticket::getCreatedAt).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<Ticket> getUnassignedTickets() {
        return tickets.values().stream()
                .filter(ticket -> ticket.getAssignedTo() == null || ticket.getAssignedTo().isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public List<Ticket> getOverdueTickets() {
        return tickets.values().stream()
            .filter(ticket -> 
                ticket.getCreatedAt() != null &&  // Check for null creation date
                !"RESOLVED".equals(ticket.getStatus()) &&
                ticket.getCreatedAt().before(getOverdueThreshold()))
            .collect(Collectors.toList());
    }

    private Date getOverdueThreshold() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, -24);
        return cal.getTime();
    }
}