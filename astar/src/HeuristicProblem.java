// S = state type, A = action type
public interface HeuristicProblem<S, A> extends Problem<S, A> {
  int estimate(S state);  // optimistic estimate of cost from state to goal
}
