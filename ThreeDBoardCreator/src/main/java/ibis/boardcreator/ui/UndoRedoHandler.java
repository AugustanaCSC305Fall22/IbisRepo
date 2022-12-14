package ibis.boardcreator.ui;

import java.util.Stack;

// Assistance: From the Drawing App class from exam 2 practice

import ibis.boardcreator.datamodel.Grid;
import ibis.boardcreator.ui.MainEditorController.EditorState;

public class UndoRedoHandler {

	private Stack<MainEditorController.EditorState> undoStack, redoStack;

	/**
	 * Constructor creates the undo stack and redo stack and pushes the start state
	 * to the undo stack which is a copy of the current state of the grid.
	 */
	public UndoRedoHandler(MainEditorController.EditorState startState) {
		undoStack = new Stack<MainEditorController.EditorState>();
		redoStack = new Stack<MainEditorController.EditorState>();

		// store the initial state of the canvas on the undo stack

		undoStack.push(startState);
	}

	/**
	 * saves the current state of the grid for later restoration
	 */
	public void saveState(MainEditorController.EditorState curState) {
		undoStack.push(curState);
		redoStack.clear();
	}

	/**
	 * Returns the previous state from the undo stack if there's only the current
	 * state on the stack, it gets returned but not removed from the undo stack
	 */
	public EditorState undo() {
		if (undoStack.size() == 1) // only the current state is on the stack
			return undoStack.peek();

		EditorState currentState = undoStack.pop();
		redoStack.push(currentState);

		return undoStack.peek();
	}

	/**
	 * returns the state of the grid from before the last undo action was performed.
	 * If some change was made to the Grid since the last undo, then this method
	 * just returns the current state.
	 */
	public EditorState redo() {
		if (redoStack.isEmpty())
			return undoStack.peek();

		EditorState currentState = redoStack.pop();
		undoStack.push(currentState);
		return currentState;

	}

}
