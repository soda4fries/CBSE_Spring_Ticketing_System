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

    public static class Result<T> {
        private T data;
        private String message;
        private boolean success;
        private String operation;
        private long timestamp;

        public Result(T data, String message, boolean success, String operation) {
            this.data = data;
            this.message = message;
            this.success = success;
            this.operation = operation;
            this.timestamp = System.currentTimeMillis();
        }

        public static <T> Result<T> success(T data, String operation) {
            return new Result<>(data, "Operation successful", true, operation);
        }

        public static <T> Result<T> success(T data, String message, String operation) {
            return new Result<>(data, message, true, operation);
        }

        public static <T> Result<T> error(String message, String operation) {
            return new Result<>(null, message, false, operation);
        }

        // Getters and setters
        public T getData() { return data; }
        public void setData(T data) { this.data = data; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getOperation() { return operation; }
        public void setOperation(String operation) { this.operation = operation; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }

    @PostMapping
    public ResponseEntity<Result<Ticket>> createTicket(@RequestParam String title, @RequestParam String description) {
        Ticket ticket = ticketService.createTicket(title, description);
        return ResponseEntity.ok(Result.success(ticket, "Ticket created successfully", "CREATE_TICKET"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Result<Ticket>> getTicket(@PathVariable String id) {
        Ticket ticket = ticketService.getTicket(id);
        if (ticket == null) {
            return ResponseEntity.status(404).body(Result.error("Ticket not found", "GET_TICKET"));
        }
        return ResponseEntity.ok(Result.success(ticket, "GET_TICKET"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Result<Ticket>> updateTicket(@PathVariable String id, @RequestBody Ticket ticket) {
        if (!id.equals(ticket.getId())) {
            return ResponseEntity.badRequest().body(Result.error("ID mismatch", "UPDATE_TICKET"));
        }
        try {
            ticketService.updateTicket(ticket);
            return ResponseEntity.ok(Result.success(ticket, "Ticket updated successfully", "UPDATE_TICKET"));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Result.error("Ticket not found", "UPDATE_TICKET"));
        }
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<Result<Ticket>> assignTicket(@PathVariable String id, @RequestParam String userId) {
        try {
            Ticket updatedTicket = ticketService.assignTicket(id, userId);
            return ResponseEntity.ok(Result.success(updatedTicket, "Ticket assigned successfully", "ASSIGN_TICKET"));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Result.error("Ticket not found", "ASSIGN_TICKET"));
        }
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<Result<Ticket>> resolveTicket(@PathVariable String id) {
        try {
            Ticket resolvedTicket = ticketService.resolveTicket(id);
            return ResponseEntity.ok(Result.success(resolvedTicket, "Ticket resolved successfully", "RESOLVE_TICKET"));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Result.error("Ticket not found", "RESOLVE_TICKET"));
        }
    }

    @PostMapping("/{ticketId}/replies")
    public ResponseEntity<Result<Reply>> addReply(
            @PathVariable String ticketId,
            @RequestParam String content,
            @RequestParam(required = false) String parentReplyId) {
        try {
            Reply reply = ticketService.addReply(ticketId, content, parentReplyId);
            return ResponseEntity.ok(Result.success(reply, "Reply added successfully", "ADD_REPLY"));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Result.error("Ticket not found", "ADD_REPLY"));
        }
    }

    @PutMapping("/{ticketId}/replies/{replyId}")
    public ResponseEntity<Result<Reply>> editReply(
            @PathVariable String ticketId,
            @PathVariable String replyId,
            @RequestParam String newContent) {
        try {
            Reply updatedReply = ticketService.editReply(ticketId, replyId, newContent);
            return ResponseEntity.ok(Result.success(updatedReply, "Reply edited successfully", "EDIT_REPLY"));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Result.error("Reply not found", "EDIT_REPLY"));
        }
    }

    @GetMapping
    public ResponseEntity<Result<List<Ticket>>> getAllTickets() {
        return ResponseEntity.ok(Result.success(ticketService.getAllTickets(), "GET_ALL_TICKETS"));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Result<List<Ticket>>> getTicketsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(Result.success(ticketService.getTicketsByStatus(status), "GET_TICKETS_BY_STATUS"));
    }

    @GetMapping("/assignee/{userId}")
    public ResponseEntity<Result<List<Ticket>>> getTicketsByAssignee(@PathVariable String userId) {
        return ResponseEntity.ok(Result.success(ticketService.getTicketsByAssignee(userId), "GET_TICKETS_BY_ASSIGNEE"));
    }

    @GetMapping("/departments")
    public ResponseEntity<Result<Map<String, List<Ticket>>>> getTicketsByDepartment() {
        return ResponseEntity.ok(Result.success(ticketService.getTicketsByDepartment(), "GET_TICKETS_BY_DEPARTMENT"));
    }

    @GetMapping("/search")
    public ResponseEntity<Result<List<Ticket>>> searchTickets(@RequestParam String query) {
        return ResponseEntity.ok(Result.success(ticketService.searchTickets(query), "SEARCH_TICKETS"));
    }

    @GetMapping("/{ticketId}/replies")
    public ResponseEntity<Result<List<Reply>>> getTicketRepliesTree(@PathVariable String ticketId) {
        try {
            List<Reply> replies = ticketService.getTicketRepliesTree(ticketId);
            return ResponseEntity.ok(Result.success(replies, "GET_TICKET_REPLIES"));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Result.error("Ticket not found", "GET_TICKET_REPLIES"));
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<Result<Map<String, Integer>>> getTicketStatistics() {
        return ResponseEntity.ok(Result.success(ticketService.getTicketStatistics(), "GET_STATISTICS"));
    }

    @GetMapping("/recent")
    public ResponseEntity<Result<List<Ticket>>> getRecentTickets(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(Result.success(ticketService.getRecentTickets(limit), "GET_RECENT_TICKETS"));
    }

    @GetMapping("/unassigned")
    public ResponseEntity<Result<List<Ticket>>> getUnassignedTickets() {
        return ResponseEntity.ok(Result.success(ticketService.getUnassignedTickets(), "GET_UNASSIGNED_TICKETS"));
    }

    @GetMapping("/overdue")
    public ResponseEntity<Result<List<Ticket>>> getOverdueTickets() {
        return ResponseEntity.ok(Result.success(ticketService.getOverdueTickets(), "GET_OVERDUE_TICKETS"));
    }
}