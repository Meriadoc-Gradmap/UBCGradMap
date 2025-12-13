package main

import (
	"fmt"
	"log"
	"net/http"

	"github.com/ubcgradmap/backend/api"
)

func main() {
	// TODO: Initialize course graph here

	// TODO: Setup routes
	http.HandleFunc("/api/test", testHandler)
	http.HandleFunc("/api/health", api.HealthHandler)
	http.HandleFunc("/api/getcourse", getCourseHandler)
	http.HandleFunc("/api/getallcourses", getAllCoursesHandler)

	port := "8080"
	fmt.Printf("Server starting on port %s\n", port)
	log.Fatal(http.ListenAndServe(":"+port, nil))
}

func testHandler(w http.ResponseWriter, r *http.Request) {
	// TODO: Implement test endpoint
	w.Write([]byte("200"))
}

func getCourseHandler(w http.ResponseWriter, r *http.Request) {
	// TODO: Get course parameter from query string
	// TODO: Look up course in graph
	// TODO: Return course as JSON
}

func getAllCoursesHandler(w http.ResponseWriter, r *http.Request) {
	// TODO: Get all course codes from graph
	// TODO: Return as JSON array
}
