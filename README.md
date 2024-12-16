# Spring Boot Ticket System

A robust ticket management system built with Spring Boot that handles support tickets, nested replies, and provides comprehensive ticket tracking functionality.

## Features

### Ticket Management
- Create and update tickets
- Assign tickets to agents
- Track ticket status (Open, In Progress, Resolved)
- Set and monitor ticket resolution deadlines
- Handle ticket replies with nested conversation threads

### Search and Filtering
- Search tickets by content
- Filter by status
- Filter by assignee
- Group by department
- View unassigned tickets
- Track overdue tickets

### Statistics and Reporting
- View ticket statistics by status
- Monitor department workloads
- Track resolution times
- Generate activity reports

## Technology Stack

- Java 17
- Spring Boot 3.2.0
- Maven
- Spring Web MVC
- Concurrent data structures for thread safety

## Getting Started

### Prerequisites
- Java JDK 17 or higher
- Maven 3.6 or higher

### Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/spring-ticket-system.git
cd spring-ticket-system
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## Project Structure

```
spring-ticket-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── ticketsystem/
│   │   │           ├── controller/
│   │   │           ├── model/
│   │   │           └── service/
│   │   └── resources/
│   └── test/
│       ├── java/
│       └── resources/
├── pom.xml
├── README.md
└── test-ticket-system.ps1
```

## API Endpoints

### Ticket Operations
- `POST /api/tickets` - Create a new ticket
- `GET /api/tickets/{id}` - Get a specific ticket
- `PUT /api/tickets/{id}` - Update a ticket
- `PUT /api/tickets/{id}/assign` - Assign a ticket
- `PUT /api/tickets/{id}/resolve` - Resolve a ticket

### Reply Management
- `POST /api/tickets/{ticketId}/replies` - Add a reply
- `PUT /api/tickets/{ticketId}/replies/{replyId}` - Edit a reply
- `GET /api/tickets/{ticketId}/replies` - Get reply tree

### Search and Filtering
- `GET /api/tickets/status/{status}` - Get tickets by status
- `GET /api/tickets/assignee/{userId}` - Get tickets by assignee
- `GET /api/tickets/departments` - Get tickets by department
- `GET /api/tickets/search?query={term}` - Search tickets
- `GET /api/tickets/unassigned` - Get unassigned tickets
- `GET /api/tickets/overdue` - Get overdue tickets

### Statistics
- `GET /api/tickets/statistics` - Get ticket statistics
- `GET /api/tickets/recent?limit={n}` - Get recent tickets

## Testing

The project includes comprehensive testing scripts:

### PowerShell Testing
Run the PowerShell test script:
```powershell
.\test-ticket-system.ps1
```

### Manual Testing
Use the provided curl commands in `test-commands.md` to test individual endpoints.

## Data Models

### Ticket
```java
{
    "id": "string",
    "title": "string",
    "description": "string",
    "status": "string",
    "assignedTo": "string",
    "createdAt": "date",
    "lastUpdatedAt": "date",
    "resolvedAt": "date"
}
```

### Reply
```java
{
    "id": "string",
    "content": "string",
    "parentId": "string",
    "timestamp": "date",
    "lastEditedAt": "date"
}
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.



