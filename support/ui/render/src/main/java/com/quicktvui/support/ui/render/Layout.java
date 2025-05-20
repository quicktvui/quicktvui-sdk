package com.quicktvui.support.ui.render;

public abstract class Layout implements LayoutProcess.Process {


    private RenderNode boundNode;

    private boolean applied = false;

    void setBoundNode(RenderNode boundNode){
        this.boundNode = boundNode;
    }

    LayoutProcess layoutProcess;

    public LayoutProcess getLayoutProcess(){
        if(layoutProcess == null){
            layoutProcess = new LayoutProcess();
        }
        return layoutProcess;
    }

    public boolean isApplied() {
        return applied;
    }

    public void setApplied(boolean applied) {
        this.applied = applied;
    }

    @Override
    public void apply(RenderNode child,int parentWidth, int parentHeight) {
        getLayoutProcess().apply(child,parentWidth,parentHeight);
        applied = true;
    }


    public <T extends Layout> T widthBy(int dx){
        getLayoutProcess().append(new LayoutProcess.SizeBy(dx,0));
        return (T) this;
    }

    public <T extends Layout> T heightBy(int dy){
        getLayoutProcess().append(new LayoutProcess.SizeBy(0,dy));
        return (T) this;
    }

    public <T extends Layout> T sizeBy(int dx,int dy){
        getLayoutProcess().append(new LayoutProcess.SizeBy(dx,dy));
        return (T) this;
    }

    public <T extends Layout> T translateX(int dx){
        getLayoutProcess().append(new LayoutProcess.Translate(dx,0));
        return (T) this;
    }

    public <T extends Layout> T translateY(int dy){
        getLayoutProcess().append(new LayoutProcess.Translate(0,dy));
        return (T) this;
    }

    public <T extends Layout> T translate(int dx,int dy){
        getLayoutProcess().append(new LayoutProcess.Translate(dx,dy));
        return (T) this;
    }

//    public <T extends Layout> T marginParentTop(int top){
//        getLayoutProcess().append(new LayoutProcess.MarginParentTop(top));
//        return (T) this;
//    }
//
//    public <T extends Layout> T marginParentLeft(int left){
//        getLayoutProcess().append(new LayoutProcess.MarginParentLeft(left));
//        return (T) this;
//    }

    public static class Relative extends Layout {

        public Relative alignParentBottom(){
            getLayoutProcess().append(new LayoutProcess.AlignParentBottom());
            return this;
        }

        public Relative alignParentLeft(){
            getLayoutProcess().append(new LayoutProcess.AlignParentLeft());
            return this;
        }

        public Relative alignParentTop(){
            getLayoutProcess().append(new LayoutProcess.AlignParentTop());
            return this;
        }

        public Relative alignParentRight(){
            getLayoutProcess().append(new LayoutProcess.AlignParentRight());
            return this;
        }


        public Relative centerInParent(){
            getLayoutProcess().append(new LayoutProcess.CenterInParent());
            return this;
        }

        public Relative centerHorizontal(){
            getLayoutProcess().append(new LayoutProcess.CenterHorizontal());
            return this;
        }

        public Relative centerVertical(){
            getLayoutProcess().append(new LayoutProcess.CenterVertical());
            return this;
        }

    }


}
