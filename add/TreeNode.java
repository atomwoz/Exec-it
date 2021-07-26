package add;

import java.util.ArrayList;

public class TreeNode 
{
	String name;
	public ArrayList<TreeNode> childrens = new ArrayList<TreeNode>();
	
	public TreeNode(String name)
	{
		this.name = name;
	}
	public TreeNode(String name, ArrayList<TreeNode> childrens)
	{
		this.name = name;
		this.childrens = childrens;
	}
	public void addChildrenArr(TreeNode[] nodes)
	{
		for(var x : nodes)
		{
			childrens.add(x);
		}
	}
	@Override
	public String toString() 
	{
		return name;
	}
	public String getTree()
	{
		return getTree("",0);
	}
	String getTree(String out , int nodenum)
	{
		String output = out;
		if(nodenum == 0)
		{
			output += name + "\n";
		}
		else
		{
			output += genLine((nodenum-1)*2," ") +  "├─" +  name + "\n";
		}
		//System.out.println(name);
		for(TreeNode tn : childrens)
		{
			if(tn != null)
			{
				output = tn.getTree(output, nodenum + 1);		
			}
				
		}
		
		return output;
	}
	String genLine(int width,String s)
	{
		String out = "";
		for(int i=0; i<width; i++)
		{
			out += s;
		}
		return out;
	}
}
