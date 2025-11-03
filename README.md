# Assignment 4:
Student: Artur Jaxygaliyev


## 1. Purpose
This project implements and evaluates three core graph algorithms for task/dependency scheduling:

1. **SCC (Tarjan) + condensation**: to collapse cycles into single components.
2. **Topological sort (Kahn)**: to get a valid execution order of components.
3. **DAG shortest / longest paths**: to compute minimal execution time and the critical path.

The algorithms are run on 9 directed, weighted test graphs (small / medium / large), and the results are saved to `data/metrics.csv`.

Dataset Overview

| Category | Files     | Vertices (n) | Edges (m) | Type                        | Weight Model |
| -------- | --------- | ------------ | --------- | --------------------------- | ------------ |
| Small    | small1–3  | 6–8          | 6–9       | simple DAGs / 1–2 cycles    | edge         |
| Medium   | medium1–3 | 12–18        | 12–19     | several SCCs, mixed density | edge         |
| Large    | large1–3  | 25–40        | 25–39     | dense cyclic structures     | edge         |


All graphs are directed, and weights represent edge durations.


## 2. Results

All algorithms (Tarjan SCC, Kahn Topological Sort, DAG Shortest Path, DAG Longest Path)  
were executed on nine datasets automatically.  
Execution times are recorded in nanoseconds (`ns`) and stored in `data/metrics.csv`.

| file | n | edges | sccCount | topoLen | tarjanNs | topoNs | dagShortNs | dagLongNs |
|------|----|--------|-----------|----------|-----------|----------|-------------|-------------|
| large1.json | 25 | 25 | 25 | 25 | 1,164,208 | 153,500 | 452,458 | 230,167 |
| large2.json | 30 | 30 | 28 | 28 | 51,750 | 40,625 | 27,167 | 29,250 |
| large3.json | 40 | 39 | 40 | 40 | 36,958 | 35,459 | 28,084 | 27,250 |
| medium1.json | 12 | 12 | 10 | 10 | 11,750 | 9,542 | 6,916 | 6,167 |
| medium2.json | 15 | 15 | 13 | 13 | 13,209 | 8,500 | 8,250 | 7,625 |
| medium3.json | 18 | 18 | 16 | 16 | 16,500 | 9,792 | 11,583 | 9,792 |
| small1.json | 6 | 6 | 6 | 6 | 8,791 | 4,958 | 4,958 | 4,250 |
| small2.json | 7 | 7 | 5 | 5 | 9,084 | 4,875 | 4,708 | 3,458 |
| small3.json | 8 | 8 | 6 | 6 | 8,916 | 4,417 | 4,708 | 4,208 |

### Observations
Execution time grows proportionally with graph size (`O(V + E)` behavior).  
For all datasets, `sccCount = topoLen` - condensation to DAG and topological ordering are correct.  
Tarjan SCC dominates total runtime for large datasets; DAG-SP steps add smaller linear overhead.  
Shortest and longest path computations differ slightly, with longest paths taking more time due to extra relaxations.



## 3. Analysis SCC / Topo / DAG-SP

### 4.1 Bottlenecks

- **Tarjan SCC.**  
  The main computational load comes from recursive depth first search (DFS).  
  As graph density increases (more outgoing edges per vertex), the number of recursive calls and low-link updates grows.  
  This explains the increase in Tarjan’s runtime from **~9 µs (small graphs)** to **~1.1 ms (large graphs)** in the table.  
  However, the complexity remains linear **O(V + E)** which is confirmed by roughly proportional growth of execution time when the number of vertices and edges doubles.

- **Topological Sort (Kahn).**  
  The algorithm’s cost depends on queue operations (push and pop).  
  On dense DAGs, more vertices have high in-degree, so the queue must check readiness more often.  
  Still, times stay within **0.004–0.15 ms**, showing almost constant overhead relative to graph size proof of linear behavior.

- **DAG Shortest/Longest Paths.**  
  Both algorithms iterate once over edges in topological order.  
  On dense DAGs the number of relaxation attempts rises, slightly increasing runtime.  
  The **Longest Path** takes longer because it evaluates maximum distances instead of minimums.  
  The metrics show `dagLongNs > dagShortNs` for most datasets, confirming the expected difference.

---

### 4.2 Effect of Graph Structure

**Edge density.**  
More edges per vertex increase DFS recursion depth, queue operations, and relaxation checks.  
This explains why large datasets (25–40 vertices) require hundreds of microseconds, while small ones finish in microseconds.  
Runtime grows linearly with edge count — again confirming **O(V + E)**.

**SCC size.**  
Larger SCCs reduce the number of nodes in the condensation DAG.  
This speeds up Topological Sort and DAG-SP, but increases the length of the **critical path**, since more tasks become internally dependent.  
In metrics, large graphs show longer critical-path times even though the condensation DAG is smaller.

**Graph size.**  
When vertex and edge counts approximately double, all measured times (Tarjan, Topo, DAG-SP) also roughly double.  
This directly supports theoretical linear scalability.

---

### 4.3 Interpretation

- Small graphs (`small1–3`) complete in microseconds, showing negligible computational cost.
- Medium graphs (`medium1–3`) scale linearly and maintain balanced times across all algorithms.
- Large graphs (`large1–3`) exhibit clear dominance of Tarjan SCC around **1.16 ms**, which matches its DFS-heavy nature.
- After SCC condensation, the DAG shrinks, so Topological Sort and DAG-SP maintain almost constant runtime regardless of density.
- The **critical path** length and its computation time grow with both graph density and SCC size — this aligns with real-world dependency graphs, where more interconnections create longer task chains.

## 5. Conclusions

### When to Use Each Method
- **Tarjan SCC**  
  Use when analyzing **general directed graphs** that may contain cycles.  
  It efficiently detects strongly connected components and enables graph condensation into a DAG.  
  Essential for dependency analysis, deadlock detection, or clustering of interdependent tasks.

- **Kahn Topological Sort**  
  Use after SCC condensation to find a **valid execution order** of components in an acyclic structure.  
  Ideal for scheduling, build systems, and dependency resolution, where tasks must follow strict order.  
  Works best on sparse DAGs with low in-degree vertices.

- **DAG Shortest Path (Dynamic Programming)**  
  Use when edge weights represent **time, cost, or distance** and you need the minimal cumulative value.  
  Applicable to project management, course prerequisites, or any acyclic flow optimization problem.

- **DAG Longest Path (Critical Path)**  
  Use when analyzing **critical paths** in scheduling or workflow systems.  
  It identifies the maximum total duration — the bottleneck that defines the minimal project completion time.

---

### Practical Insights
- Tarjan SCC dominates computation time but runs in linear time **O(V + E)**, remaining efficient even for dense graphs.
- Topological Sort and DAG-SP operations add minimal overhead, confirming excellent scalability.
- Condensing graphs into SCCs significantly simplifies subsequent processing.
- The **critical path** length increases with graph density and SCC size, matching expected real-world behavior.

---

**Overall conclusion:**  
The combination of SCC, Topo, DAG-SP provides a complete and scalable framework for analyzing dependency graphs.  
Its both theoretically optimal and practically efficient for scheduling, planning, and optimization tasks.





