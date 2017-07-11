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
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.ChaseCamera;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author josephmontanez
 */
public class Level01State extends AbstractAppState {

    private final Node rootNode;
    private BulletAppState bulletAppState;
    /**
     * This will hold all of our scene details and make it easier to clean up when we change to the next level
     */
    private final Node localRootNode = new Node("Level 1");
    private final AssetManager assetManager;
    private final InputManager inputManager;
    private final FlyByCamera flyByCamera;
    private final Camera camera;
    private ChaseCamera chaseCam;
    private CharacterControl playerControl;
    
    //-- Setup walk direction
    private final Vector3f playerWalkDirection = new Vector3f(0, 0, 0);
    
    //-- Setup directional booleans
    private boolean left = false, right = false, up = false, down = false;
    
    public Level01State(SimpleApplication app) {
        rootNode = app.getRootNode();
        assetManager = app.getAssetManager();
        inputManager = app.getInputManager();
        
        //-- Get the fly by camera to disable it
        flyByCamera = app.getFlyByCamera();
        
        //-- Get the camera to get where the camera is looking
        camera = app.getCamera();
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        //-- Setup physics
        bulletAppState = new BulletAppState();
        //-- Turn on debug to see shapes of physics
        bulletAppState.setDebugEnabled(true);
        //-- Attach to state manager
        stateManager.attach(bulletAppState);
        
        rootNode.attachChild(localRootNode);
        
        //-- Load scene
        Spatial scene = assetManager.loadModel("Scenes/Level1.j3o");

        //-- Add scene to local node
        localRootNode.attachChild(scene);
        
        
        //-- Locate Player
        Geometry player = (Geometry) localRootNode.getChild("Player");
        
        //-- Get bounding box
        BoundingBox boundingBox = (BoundingBox) player.getWorldBound();
        
        //-- Use bound box to find radius and height of player
        float radius = boundingBox.getXExtent();
        float height = boundingBox.getYExtent();
        
        //-- Setup the collision shape
        CapsuleCollisionShape shape = new CapsuleCollisionShape(radius, height);

        //-- Setup the playerControl
        playerControl = new CharacterControl(shape, 1.0f);
        
        //-- Manually add the control to the player
        player.addControl(playerControl);
        
        //-- Now add it to the bulletAppState
        bulletAppState.getPhysicsSpace().add(playerControl);
        
        //-- Find the floor
        Geometry floor = (Geometry) localRootNode.getChild("Floor");
        
        //-- The physics control is already added so lets just fetch it
        RigidBodyControl floorControl = floor.getControl(RigidBodyControl.class);
        
        //-- Then add it to the bulletAppState
        bulletAppState.getPhysicsSpace().add(floorControl);
        
        inputManager.addMapping("Pause", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(actionListener, "Pause", "Up", "Down", "Left", "Right", "Jump");
        
        
        flyByCamera.setEnabled(false);
        chaseCam = new ChaseCamera(camera, player, inputManager);
        chaseCam.setInvertVerticalAxis(true);
    }
    
    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Pause") && !keyPressed) {
                setEnabled(!isEnabled());
            } else if (name.equals("Up")) {
                up = keyPressed;
            } else if (name.equals("Down")) {
                down = keyPressed;
            } else if (name.equals("Left")) {
                left = keyPressed;
            } else if (name.equals("Right")) {
                right = keyPressed;
            } else if (name.equals("Jump")) {
                playerControl.jump();
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
        Vector3f camDir = camera.getDirection().clone();
        Vector3f camLeft = camera.getLeft().clone();
        camDir.y = 0;
        camLeft.y = 0;
        camDir.normalizeLocal();
        camLeft.normalizeLocal();
        playerWalkDirection.set(0, 0, 0);

        if (left) playerWalkDirection.addLocal(camLeft);
        if (right) playerWalkDirection.addLocal(camLeft.negate());
        if (up) playerWalkDirection.addLocal(camDir);
        if (down) playerWalkDirection.addLocal(camDir.negate());
        
        Geometry player = (Geometry) localRootNode.getChild("Player");
        if (player != null) {
            playerWalkDirection.multLocal(10).multLocal(tpf);
            playerControl.setWalkDirection(playerWalkDirection);
        }
        
    }
}
