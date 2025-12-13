package graph

import (
	"gonum.org/v1/gonum/graph/simple"
)

const (
	defaultGrade = 75.0 // default grade for courses with missing data
	defaultHours = 5.0  // default weekly hours for courses with missing schedule data
)

// CreateWeightedCourseGraph constructs a weighted directed graph where
// each course is a node and edges point from prerequisite -> course
// with a weight determined by `edgeWeight`.
func CreateCourseGraph(courses []Course) *simple.WeightedDirectedGraph {
	g := simple.NewWeightedDirectedGraph(0, 0)

	// assign incremental IDs to courses and add nodes
	codeToID := make(map[string]int64, len(courses))
	for _, c := range courses {
		n := g.NewNode()
		g.AddNode(n)
		codeToID[c.Code] = n.ID()
	}

	// add edges from prerequisite -> course
	for _, c := range courses {
		toID := codeToID[c.Code]
		for _, pre := range c.Prerequisites {
			if fromID, ok := codeToID[pre]; ok {
				// Skip self-edges (course listed as its own prerequisite)
				if fromID == toID {
					continue
				}
				fromNode := g.Node(fromID)
				toNode := g.Node(toID)
				if fromNode == nil || toNode == nil {
					continue
				}
				g.SetWeightedEdge(g.NewWeightedEdge(fromNode, toNode, edgeWeight(c)))
			}
		}
	}

	return g
}

// edgeWeight calculates the weight for an edge to course `c`.
// Combines difficulty (inverse of grade) with workload (hours per week).
// Formula: base_difficulty + workload_factor
//   - base: 101 - avg_grade (higher grade = lower difficulty)
//   - workload: total hours/week from lectures, labs, tutorials
//   - fallback: uses defaults defined as constants at top of file
func edgeWeight(c Course) float64 {
	grade := c.Others.Grade
	if grade <= 0 {
		grade = defaultGrade
	}

	baseDifficulty := 101.0 - grade

	totalHours := float64(c.Schedule.Lectures + c.Schedule.Labs + c.Schedule.Tutorials)
	if totalHours <= 0 {
		totalHours = defaultHours
	}

	workloadFactor := 0.5 * totalHours

	return baseDifficulty + workloadFactor
}
