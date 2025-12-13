package api

import (
	"encoding/json"
	"net/http"
)

// Health represents a simple health status payload
type Health struct {
	Status string `json:"status"`
}

// HealthHandler returns a basic health check JSON
// GET /api/health -> {"status":"ok"}
func HealthHandler(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	// Allow CORS for simplicity while learning
	w.Header().Set("Access-Control-Allow-Origin", "*")

	resp := Health{Status: "ok"}
	_ = json.NewEncoder(w).Encode(resp)
}
