# PERT
Project management tool used to schedule, organize, and coordinate tasks within a project. In a project, one action generally depends on the successful completion of another. Hence, we need schedules and task completion times to organise the project efficiently. 
These relationship can be captured by a directed acyclic graph(DAG).Vertices of the graph represent the project milestones and the edges represent the tasks that have to be performed between the milestones. The topological sort gives us the critical path for the project milestones. It gives the time needed to complete the milestone/task. Thus, schedulling can be done with topological order of a graph. 

## Operations

For a task u:

	public int ec(Vertex u);            // Earliest completion time of u
	public int lc(Vertex u);            // Latest completion time of u
	public int slack(Vertex u);         // Slack of u
	public int criticalPath();          // Length of critical path
	public boolean critical(Vertex u);  // Is vertex u on a critical path?
	public int numCritical();           // Number of critical nodes in graph

## References
[Details on PERT](https://en.wikipedia.org/wiki/Program_evaluation_and_review_technique)
[Topological Sort](https://en.wikipedia.org/wiki/Program_evaluation_and_review_technique)
[Depth First Search](https://medium.com/basecs/deep-dive-through-a-graph-dfs-traversal-8177df5d0f13)
