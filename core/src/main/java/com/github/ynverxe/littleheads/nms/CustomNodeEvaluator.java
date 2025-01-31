package com.github.ynverxe.littleheads.nms;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.*;

public class CustomNodeEvaluator extends FlyNodeEvaluator {

  @Override
  public Node getStart() {
    return super.getStart();
  }

  @Override
  public int getNeighbors(Node[] successors, Node center) {
    int count = super.getNeighbors(successors, center);
    if (true) return count;
    int[][] directions = {
        {1, 2, 0},
        {-1, 2, 0},
        {0, 2, 1},
        {0, 2, -1},
        {0, 2, 0}
    };

    for (int[] direction : directions) {
      Node node = findAcceptedNode(center.x + direction[0], center.y + direction[1], center.z + direction[2]);

      if (!isValidNode(node)) continue;

      //System.out.println("Decrase cost");
      node.costMalus -= 4;

      successors[count++] = node;
    }

    return count;
  }

  private boolean isValidNode(Node node) {
    Level level = mob.level();

    if (node == null || node.closed) {
      return false;
    }

    BlockState blockAtNode = level.getBlockState(node.asBlockPos());
    BlockState below = level.getBlockState(node.asBlockPos().below());
    BlockState below2 = level.getBlockState(node.asBlockPos().below(2));
    BlockState below3 = level.getBlockState(node.asBlockPos().below(3));

    return !blockAtNode.isSolid() && !below.isSolid() && below2.isSolid();
  }
}