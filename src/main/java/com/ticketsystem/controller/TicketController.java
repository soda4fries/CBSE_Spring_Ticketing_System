package com.ticketsystem.controller;

import com.ticketsystem.model.Ticket;
import com.ticketsystem.model.Reply;
import com.ticketsystem.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    private final TicketService ticketService;

    @Autowired
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestParam String title, @RequestParam String description) {
        return ResponseEntity.ok(ticketService.createTicket(title, description));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicket(@PathVariable String id) {
        Ticket ticket = ticketService.getTicket(id);
        return ticket != null ? ResponseEntity.ok(ticket) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTicket(@PathVariable String id, @RequestBody Ticket ticket) {
        if (!id.equals(ticket.getId())) {
            return ResponseEntity.badRequest().build();
        }
        ticketService.updateTicket(ticket);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<Void> assignTicket(@PathVariable String id, @RequestParam String userId) {
        ticketService.assignTicket(id, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<Void> resolveTicket(@PathVariable String id) {
        ticketService.resolveTicket(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{ticketId}/replies")
    public ResponseEntity<Reply> addReply(
            @PathVariable String ticketId,
            @RequestParam String content,
            @RequestParam(required = false) String parentReplyId) {
        return ResponseEntity.ok(ticketService.addReply(ticketId, content, parentReplyId));
    }

    @PutMapping("/{ticketId}/replies/{replyId}")
    public ResponseEntity<Void> editReply(
            @PathVariable String ticketId,
            @PathVariable String replyId,
            @RequestParam String newContent) {
        ticketService.editReply(ticketId, replyId, newContent);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Ticket>> getTicketsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(ticketService.getTicketsByStatus(status));
    }

    @GetMapping("/assignee/{userId}")
    public ResponseEntity<List<Ticket>> getTicketsByAssignee(@PathVariable String userId) {
        return ResponseEntity.ok(ticketService.getTicketsByAssignee(userId));
    }

    @GetMapping("/departments")
    public ResponseEntity<Map<String, List<Ticket>>> getTicketsByDepartment() {
        return ResponseEntity.ok(ticketService.getTicketsByDepartment());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Ticket>> searchTickets(@RequestParam String query) {
        return ResponseEntity.ok(ticketService.searchTickets(query));
    }

    @GetMapping("/{ticketId}/replies")
    public ResponseEntity<List<Reply>> getTicketRepliesTree(@PathVariable String ticketId) {
        return ResponseEntity.ok(ticketService.getTicketRepliesTree(ticketId));
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Integer>> getTicketStatistics() {
        return ResponseEntity.ok(ticketService.getTicketStatistics());
    }

    @GetMapping("/recent")
    public ResponseEntity<List<Ticket>> getRecentTickets(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(ticketService.getRecentTickets(limit));
    }

    @GetMapping("/unassigned")
    public ResponseEntity<List<Ticket>> getUnassignedTickets() {
        return ResponseEntity.ok(ticketService.getUnassignedTickets());
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<Ticket>> getOverdueTickets() {
        return ResponseEntity.ok(ticketService.getOverdueTickets());
    }
}