from collections import defaultdict
import sys

class Graph:
    def __init__(self, ns, es):
        self.totalEncountered = 0
        self.nodes = ns
        self.edges = es
        self.graph = defaultdict(list)
        self.components = [[]]

    def create(self):
        for i in range(self.edges):
            edge_input = map(int, raw_input().split())
            self.add_edge(edge_input[0] - 1, edge_input[1] - 1)

        return self

    def add_edge(self, src, dest):
        self.graph[src].append(dest)

    def reverse(self):
        g = Graph(self.edges, self.nodes)
        for i in self.graph:
            for j in self.graph[i]:
                g.add_edge(j, i)
        return g

    def dfs(self, src):
        visited = [False] * self.nodes
        self.dfs_helper(src, visited)
        return self.totalEncountered

    def dfs_helper(self, src, visited):
        visited[src] = True
        for i in self.graph[src]:
            if visited[i] is False:
                self.totalEncountered += 1
                self.dfs_helper(i, visited)

    def num_capitals(self):
        # first, run dfs once on original graph and add finishing times
        node_stack = []
        visited = [False] * self.nodes
        for i in range(self.nodes):
            if visited[i] is False:
                self.dfs_add_times(i, visited, node_stack)

        # then, reverse graph, run dfs, choosing starting nodes in descending finishing time
        # they were added to stack in ascending, so by popping, we get nodes in descending order
        transposed = self.reverse()
        visited = [False] * transposed.nodes
        num_components = 0
        while node_stack:
            i = node_stack.pop()
            if visited[i] is False:
                transposed.components_helper(num_components, i, visited)
                transposed.components.append([])
                num_components += 1

        # grab a candidate root node from the last strongly connected component
        temp_components = transposed.components

        # remove tailing empty array
        temp_components.remove([])

        last_component_found = temp_components[len(temp_components) - 1]
        candidate_root = last_component_found[len(last_component_found) - 1]

        num_roots = transposed.dfs(candidate_root)
        if num_roots == self.nodes - 1:
            return len(last_component_found)
        else:
            return 0

    def components_helper(self, component_num, src, visited):
        visited[src] = True
        self.components[component_num].append(src)
        for i in self.graph[src]:
            if visited[i] is False:
                self.components_helper(component_num, i, visited)

    def dfs_add_times(self, src, visited, stack):
        visited[src] = True
        for i in self.graph[src]:
            if visited[i] is False:
                self.dfs_add_times(i, visited, stack)
        stack.append(src)

if __name__ == "__main__":
    sys.setrecursionlimit(100000)

    basic_info = map(int, raw_input().split())
    routes = basic_info[1]
    cities = basic_info[0]
    kingdom = Graph(cities, routes).create()
    print(kingdom.num_capitals())
    