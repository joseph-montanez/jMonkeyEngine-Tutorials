/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cosmicgrams.helloworld1.state;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Node;

/**
 *
 * @author josephmontanez
 */
public class Level01State extends AbstractAppState {

    private final Node rootNode;
    /**
     * This will hold all of our scene details and make it easier to clean up when we change to the next level
     */
    private final Node localRootNode = new Node("Level 1");
    
    public Level01State(SimpleApplication app) {
        rootNode = app.getRootNode();
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        rootNode.attachChild(localRootNode);
    }
    
    @Override
    public void cleanup() {
        rootNode.detachChild(localRootNode);
        
        super.cleanup();
    }
}
