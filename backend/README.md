# UBCGradMap Go Backend

Minimal Go backend skeleton for learning.

## Structure

```
backend/
├── main.go           # Entry point with HTTP server
├── go.mod            # Go module definition
├── api/              # HTTP handlers
├── graph/            # Course graph data structures
│   ├── course.go     # Course struct
│   └── graph.go      # Graph operations
├── db/               # Database (future)
└── scraper/          # Web scraping (future)
```

## Running

```bash
cd backend
go run main.go
```

## Next Steps

1. Implement the TODO items in each file
2. Add dependencies to go.mod as needed (e.g., `go get github.com/gorilla/mux`)
3. Test endpoints: `curl http://localhost:8080/api/test`
