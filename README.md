# SkipList

A SkipList is a data structure that is essentially a linked list, but with a twist. Each node of the SkipList has a height which is randomly assigned to each node upon creation. The SkipList's height is directly related to the amount of nodes in the list. The max amount of nodes in the list is equal to 2^n, where n is equal to the height of the list. This is necessary in order for the list to have optimized search, delete, add and contains functionality.

This ratio of height to node allows the SkipList on average to have a O(n log n) runtime for its primary functions by skipping (hence the name) nodes at the highest level first, and working its way down until it reaches the correct node for the function it is performing. Maintaining this ratio is crucial, because if not, the desired runtime will not work as advertised. The functions are built in such a way that they once the thresehold for adding or reducing the max height is reached, the SkipList will automatically randomly increase the height of its tallest nodes, or decrease the heights of the tallest nodes in order to maintain the structural integrity of the data structure.

This data structure is also designed to accept any object type that implements the *Comparable* interface, which is necessary for the decision making logic in the object's functions.
