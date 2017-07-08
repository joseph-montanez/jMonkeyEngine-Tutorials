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
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

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
    private final AssetManager assetManager;
    private final InputManager inputManager;
    
    public Level01State(SimpleApplication app) {
        rootNode = app.getRootNode();
        assetManager = app.getAssetManager();
        inputManager = app.getInputManager();
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        rootNode.attachChild(localRootNode);
        
        
        Box b = new Box(1, 1, 1);
        Geometry geom = new Geometry("Box", b);

        Material mat = assetManager.loadMaterial("Materials/BlueBoat.j3m");
        geom.setMaterial(mat);

        localRootNode.attachChild(geom);
        
        inputManager.addMapping("Pause", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addListener(actionListener, "Pause");
    }
    
    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Pause") && !keyPressed) {
                setEnabled(!isEnabled());
            }
        }
    };
    
    @Override
    public void cleanup() {
        rootNode.detachChild(localRootNode);
        
        super.cleanup();
    }
    
    @Override
    public void update(float tpf) {
        Geometry geom = (Geometry) localRootNode.getChild("Box");
        if (geom != null) {
            float speed = 1.0f;
            float addXRot = speed * tpf;
            geom.rotate(addXRot, 0, 0);
        }
        
    }
}
