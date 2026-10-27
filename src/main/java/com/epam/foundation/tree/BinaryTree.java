package com.epam.foundation.tree;

import java.util.*;

/**
 * Binary Search Tree with traversal (bypass) algorithms:
 * In-Order, Pre-Order, Post-Order, and Level-Order (BFS).
 */
public class BinaryTree<T extends Comparable<T>> {

    public static class Node<T> {
        T data;
        Node<T> left;
        Node<T> right;

        Node(T data) {
            this.data = data;
        }
    }

    private Node<T> root;

    public void insert(T data) {
        root = insertRec(root, data);
    }

    private Node<T> insertRec(Node<T> current, T data) {
        if (current == null) return new Node<>(data);
        if (data.compareTo(current.data) < 0) {
            current.left = insertRec(current.left, data);
        } else if (data.compareTo(current.data) > 0) {
            current.right = insertRec(current.right, data);
        }
        return current;
    }

    // In-Order Bypass: Left -> Root -> Right (produces sorted sequence)
    public List<T> inOrderTraversal() {
        List<T> result = new ArrayList<>();
        inOrderRec(root, result);
        return result;
    }

    private void inOrderRec(Node<T> node, List<T> list) {
        if (node != null) {
            inOrderRec(node.left, list);
            list.add(node.data);
            inOrderRec(node.right, list);
        }
    }

    // Pre-Order Bypass: Root -> Left -> Right
    public List<T> preOrderTraversal() {
        List<T> result = new ArrayList<>();
        preOrderRec(root, result);
        return result;
    }

    private void preOrderRec(Node<T> node, List<T> list) {
        if (node != null) {
            list.add(node.data);
            preOrderRec(node.left, list);
            preOrderRec(node.right, list);
        }
    }

    // Post-Order Bypass: Left -> Right -> Root
    public List<T> postOrderTraversal() {
        List<T> result = new ArrayList<>();
        postOrderRec(root, result);
        return result;
    }

    private void postOrderRec(Node<T> node, List<T> list) {
        if (node != null) {
            postOrderRec(node.left, list);
            postOrderRec(node.right, list);
            list.add(node.data);
        }
    }

    // Level-Order Bypass (Breadth-First Search)
    public List<T> levelOrderTraversal() {
        List<T> result = new ArrayList<>();
        if (root == null) return result;

        Queue<Node<T>> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            Node<T> current = queue.poll();
            result.add(current.data);
            if (current.left != null) queue.add(current.left);
            if (current.right != null) queue.add(current.right);
        }
        return result;
    }
}
