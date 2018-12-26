/*
 *
 *  *
 *  *  * This file is part of CuneiformWordSegmenter.
 *  *  *
 *  *  *   CuneiformWordSegmenter is free software: you can redistribute it and/or modify
 *  *  *     it under the terms of the GNU General Public License as published by
 *  *  *     the Free Software Foundation, either version 3 of the License, or
 *  *  *     (at your option) any later version.
 *  *  *
 *  *  *    CuneiformWordSegmenter is distributed in the hope that it will be useful,
 *  *  *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  *  *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  *  *     GNU General Public License for more details.
 *  *  *
 *  *  *     You should have received a copy of the GNU General Public License
 *  *  *     along with CuneiformWordSegmenter.  If not, see <http://www.gnu.org/licenses/>.
 *  *  *
 *  *
 *
 */

package de.unifrankfurt.cs.acoli.akkad.main.gui.tool;

import org.abego.treelayout.Configuration;
import org.abego.treelayout.NodeExtentProvider;
import org.abego.treelayout.TreeForTreeLayout;
import org.abego.treelayout.TreeLayout;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import java.util.Enumeration;

/**
 * Created by timo on 10/22/14.
 */
public class SentenceScreenMain extends JFrame {

    private JPanel treePanel;

    public SentenceScreenMain(TreeForTreeLayout<TreeNode> start,NodeExtentProvider<TreeNode> extentProvider,Configuration<TreeNode> configuration){
        this.treePanel=new JPanel();
        TreeLayout<TreeNode> layout=new TreeLayout<TreeNode>(start,extentProvider,configuration);
        //this.treePanel.add(layout.);

    }



}
