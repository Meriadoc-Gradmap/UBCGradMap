package api

import (
	"encoding/json"
	"net/http"

	"gonum.org/v1/gonum/graph/simple"
)

// Handler bundles dependencies (like the course graph) for HTTP methods.
type Handler struct {
	Graph *simple.WeightedDirectedGraph
}

// Health represents a simple health status payload
type Health struct {
	Status string `json:"status"`
}

// HealthHandler returns a basic health check JSON
// GET /api/health -> {"status":"ok"}
func (h *Handler) HealthHandler(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	w.Header().Set("Access-Control-Allow-Origin", "*")
	_ = json.NewEncoder(w).Encode(Health{Status: "ok"})
}

// TestHandler returns a simple OK for testing API availability
func (h *Handler) TestHandler(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "text/plain")
	w.Write([]byte("200"))
}

// GetCourseHandler is a placeholder for course lookup
func (h *Handler) GetCourseHandler(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	// TODO: read query param, lookup course in h.Graph, return JSON
	_ = json.NewEncoder(w).Encode(map[string]string{"message": "not implemented"})
}

// GetAllCoursesHandler is a placeholder to return all course codes
func (h *Handler) GetAllCoursesHandler(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	// TODO: list all codes from h.Graph
	_ = json.NewEncoder(w).Encode([]string{})
}
