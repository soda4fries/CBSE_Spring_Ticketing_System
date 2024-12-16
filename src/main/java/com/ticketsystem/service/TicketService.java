package com.ticketsystem.service;

import com.ticketsystem.model.Reply;
import com.ticketsystem.model.Ticket;

import java.util.List;
import java.util.Map;

public interface TicketService {

    Ticket createTicket(String title, String description);

    Ticket getTicket(String id);

    Ticket updateTicket(Ticket ticket);

    Ticket assignTicket(String ticketId, String userId);

    Reply addReply(String ticketId, String content, String parentReplyId);

    Reply editReply(String ticketId, String replyId, String newContent);

    Ticket resolveTicket(String ticketId);

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
