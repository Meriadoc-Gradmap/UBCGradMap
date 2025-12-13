package api

import (
	"encoding/json"
	"net/http"
)

// TestHandler returns a simple OK for testing API availability
func TestHandler(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "text/plain")
	w.Write([]byte("200"))
}

// GetCourseHandler is a placeholder for course lookup
func GetCourseHandler(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	// TODO: read query param, lookup course, return JSON
	_ = json.NewEncoder(w).Encode(map[string]string{"message": "not implemented"})
}

// GetAllCoursesHandler is a placeholder to return all course codes
func GetAllCoursesHandler(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	// TODO: list all codes from graph
	_ = json.NewEncoder(w).Encode([]string{})
}
