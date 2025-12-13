package graph

// Course represents a university course
type Course struct {
	Code          string    `json:"code"`
	Name          string    `json:"name"`
	Credits       []float64 `json:"credits"`
	Description   string    `json:"description"`
	Prerequisites []string  `json:"prerequisites"`
	Corequisites  []string  `json:"corequisites"`
	CDF           bool      `json:"cdf"`
	Schedule      Hours     `json:"schedule"`
	Others        Others    `json:"others"`
}

// Hours represents weekly course hours
type Hours struct {
	Lectures     int  `json:"lectures"`
	Alternating1 bool `json:"alternating1"`
	Labs         int  `json:"labs"`
	Alternating2 bool `json:"alternating2"`
	Tutorials    int  `json:"tutorials"`
	Alternating3 bool `json:"alternating3"`
}

// Others represents additional course info
type Others struct {
	Grade     float64 `json:"grade"`
	Professor string  `json:"professor,omitempty"`
}
