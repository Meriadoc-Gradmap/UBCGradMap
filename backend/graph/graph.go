package graph

// CourseGraph represents the graph of courses and their relationships
type CourseGraph struct {
	courses map[string]*Course
	// TODO: Add adjacency matrix or adjacency list for prerequisites
}

// NewCourseGraph creates a new empty course graph
func NewCourseGraph() *CourseGraph {
	return &CourseGraph{
		courses: make(map[string]*Course),
	}
}

// LoadFromFile loads courses from a JSON file
func LoadFromFile(filename string) (*CourseGraph, error) {
	// TODO: Read JSON file
	// TODO: Parse courses
	// TODO: Build graph structure
	return nil, nil
}

// GetCourse retrieves a course by code
func (cg *CourseGraph) GetCourse(code string) (*Course, error) {
	// TODO: Look up course in map
	return nil, nil
}

// GetAllCodes returns all course codes
func (cg *CourseGraph) GetAllCodes() []string {
	// TODO: Return list of all course codes
	return nil
}

// GetPrerequisites returns immediate prerequisites for a course
func (cg *CourseGraph) GetPrerequisites(code string) []string {
	// TODO: Return prerequisites
	return nil
}

// GetPostrequisites returns courses that require this course
func (cg *CourseGraph) GetPostrequisites(code string) []string {
	// TODO: Return postrequisites
	return nil
}
