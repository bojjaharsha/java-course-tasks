package com.epam.foundation.tree;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BinaryTreeTest {

    @Test
    void testBypassTraversals() {
        BinaryTree<Integer> tree = new BinaryTree<>();
        // Insert order: 4, 2, 6, 1, 3, 5, 7
        List.of(4, 2, 6, 1, 3, 5, 7).forEach(tree::insert);

        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7), tree.inOrderTraversal());
        assertEquals(List.of(4, 2, 1, 3, 6, 5, 7), tree.preOrderTraversal());
        assertEquals(List.of(1, 3, 2, 5, 7, 6, 4), tree.postOrderTraversal());
        assertEquals(List.of(4, 2, 6, 1, 3, 5, 7), tree.levelOrderTraversal());
    }
}
