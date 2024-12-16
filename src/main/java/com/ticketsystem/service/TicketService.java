package com.ticketsystem.service;

import com.ticketsystem.model.Ticket;
import com.ticketsystem.model.Reply;
import java.util.List;
import java.util.Map;

public interface TicketService {
    // Creation and Updates
    Ticket createTicket(String title, String description);
    void updateTicket(Ticket ticket);
    void assignTicket(String ticketId, String userId);

    // Reply Management
    Reply addReply(String ticketId, String content, String parentReplyId);
    void editReply(String ticketId, String replyId, String newContent);

    // Status Management
    void resolveTicket(String ticketId);

    // Viewing Methods
    Ticket getTicket(String id);
    List<Ticket> getAllTickets();
    List<Ticket> getTicketsByStatus(String status);
    List<Ticket> getTicketsByAssignee(String userId);
    Map<String, List<Ticket>> getTicketsByDepartment();
    List<Ticket> searchTickets(String searchTerm);
    List<Reply> getTicketRepliesTree(String ticketId);
    Map<String, Integer> getTicketStatistics();
    List<Ticket> getRecentTickets(int limit);
    List<Ticket> getUnassignedTickets();
    List<Ticket> getOverdueTickets();
}
