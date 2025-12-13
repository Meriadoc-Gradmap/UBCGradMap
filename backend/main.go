package main

import (
	"fmt"
	"log"
	"net/http"

	"github.com/ubcgradmap/backend/api"
	"github.com/ubcgradmap/backend/graph"
)

const (
	dataLocation = "../data/COURSE_INFO.json"
)

func main() {
	// Load courses from JSON
	courses, err := graph.LoadCourses(dataLocation)
	if err != nil {
		log.Printf("❌ failed to load courses from %s: %v", dataLocation, err)
	} else {
		log.Printf("✅ loaded %d courses", len(courses))
	}

	// Build weighted course graph
	courseGraph := graph.CreateCourseGraph(courses)

	// Construct handler with dependencies
	handler := &api.Handler{Graph: courseGraph}

	// Setup routes
	http.HandleFunc("/api/test", handler.TestHandler)
	http.HandleFunc("/api/health", handler.HealthHandler)
	http.HandleFunc("/api/getcourse", handler.GetCourseHandler)
	http.HandleFunc("/api/getallcourses", handler.GetAllCoursesHandler)

	port := "8080"
	fmt.Printf("Server starting on port %s\n", port)
	log.Fatal(http.ListenAndServe(":"+port, nil))
}
