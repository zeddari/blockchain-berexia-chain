const ShipArrivalContract = artifacts.require("ShipArrivalContract");

contract("ShipArrivalContract", accounts => {
  const owner = accounts[0];
  const user = accounts[1];
  
  let shipArrivalContract;
  
  // Test data
  const testData = {
    numeroAvis: 12345,
    numeroEscale: "ESC001",
    nomPort: "Casablanca",
    codePort: "CASA",
    nomNavire: "Test Ship",
    etat: "EN_ATTENTE"
  };
  
  // Setup before each test
  beforeEach(async () => {
    shipArrivalContract = await ShipArrivalContract.new({ from: owner });
  });
  
  describe("Contract Deployment", () => {
    it("should set the right owner", async () => {
      const contractOwner = await shipArrivalContract.owner();
      assert.equal(contractOwner, owner, "Owner should be the deployer");
    });
  });
  
  describe("Ship Arrival Recording", () => {
    it("should record a new ship arrival", async () => {
      const tx = await shipArrivalContract.recordShipArrival(
        testData.numeroAvis,
        testData.numeroEscale,
        testData.nomPort,
        testData.codePort,
        testData.nomNavire,
        testData.etat,
        { from: owner }
      );
      
      // Check if the event was emitted
      const event = tx.logs.find(log => log.event === "ShipArrivalRecorded");
      assert.exists(event, "ShipArrivalRecorded event should be emitted");
      
      // Check if the ship arrival exists
      const exists = await shipArrivalContract.shipArrivalExists(testData.numeroAvis);
      assert.isTrue(exists, "Ship arrival should exist");
      
      // Get the ship arrival details
      const arrival = await shipArrivalContract.getShipArrival(testData.numeroAvis);
      assert.equal(arrival[0].toString(), testData.numeroAvis.toString(), "Numero Avis should match");
      assert.equal(arrival[1], testData.numeroEscale, "Numero Escale should match");
      assert.equal(arrival[2], testData.nomPort, "Nom Port should match");
      assert.equal(arrival[3], testData.codePort, "Code Port should match");
      assert.equal(arrival[4], testData.nomNavire, "Nom Navire should match");
      assert.equal(arrival[5], testData.etat, "Etat should match");
    });
    
    it("should allow recording multiple ship arrivals with the same numeroAvis", async () => {
      // Record the first ship arrival
      await shipArrivalContract.recordShipArrival(
        testData.numeroAvis,
        testData.numeroEscale,
        testData.nomPort,
        testData.codePort,
        testData.nomNavire,
        testData.etat,
        { from: owner }
      );
      
      // Record another ship arrival with the same numeroAvis but different details
      const tx = await shipArrivalContract.recordShipArrival(
        testData.numeroAvis,
        "ESC002",
        "Tanger",
        "TNG",
        "Another Ship",
        "EN_ATTENTE",
        { from: owner }
      );
      
      // Check if the event was emitted
      const event = tx.logs.find(log => log.event === "ShipArrivalRecorded");
      assert.exists(event, "ShipArrivalRecorded event should be emitted");
      
      // Get the updated ship arrival details
      const arrival = await shipArrivalContract.getShipArrival(testData.numeroAvis);
      assert.equal(arrival[0].toString(), testData.numeroAvis.toString(), "Numero Avis should match");
      assert.equal(arrival[1], "ESC002", "Numero Escale should be updated");
      assert.equal(arrival[2], "Tanger", "Nom Port should be updated");
      assert.equal(arrival[3], "TNG", "Code Port should be updated");
      assert.equal(arrival[4], "Another Ship", "Nom Navire should be updated");
      assert.equal(arrival[5], "EN_ATTENTE", "Etat should be updated");
    });
  });
  
  describe("Ship Arrival Updates", () => {
    it("should update an existing ship arrival", async () => {
      // Record the initial ship arrival
      await shipArrivalContract.recordShipArrival(
        testData.numeroAvis,
        testData.numeroEscale,
        testData.nomPort,
        testData.codePort,
        testData.nomNavire,
        testData.etat,
        { from: owner }
      );
      
      // Update data
      const updateData = {
        numeroEscale: "ESC002",
        nomPort: "Tanger",
        codePort: "TNG",
        nomNavire: "Updated Ship",
        etat: "ARRIVE"
      };
      
      // Update the ship arrival
      const tx = await shipArrivalContract.updateShipArrival(
        testData.numeroAvis,
        updateData.numeroEscale,
        updateData.nomPort,
        updateData.codePort,
        updateData.nomNavire,
        updateData.etat,
        { from: owner }
      );
      
      // Check if the event was emitted
      const event = tx.logs.find(log => log.event === "ShipArrivalRecorded");
      assert.exists(event, "ShipArrivalRecorded event should be emitted");
      
      // Get the updated ship arrival details
      const arrival = await shipArrivalContract.getShipArrival(testData.numeroAvis);
      assert.equal(arrival[1], updateData.numeroEscale, "Numero Escale should be updated");
      assert.equal(arrival[2], updateData.nomPort, "Nom Port should be updated");
      assert.equal(arrival[3], updateData.codePort, "Code Port should be updated");
      assert.equal(arrival[4], updateData.nomNavire, "Nom Navire should be updated");
      assert.equal(arrival[5], updateData.etat, "Etat should be updated");
    });
    
    it("should not allow updating a non-existent ship arrival", async () => {
      let errorThrown = false;
      try {
        await shipArrivalContract.updateShipArrival(
          99999, // Non-existent numeroAvis
          "ESC002",
          "Tanger",
          "TNG",
          "Another Ship",
          "EN_ATTENTE",
          { from: owner }
        );
      } catch (error) {
        errorThrown = true;
        // The exact error message might vary, so we just check that an error was thrown
        assert.isTrue(error.message.includes("revert"), "Should throw a revert error");
      }
      assert.isTrue(errorThrown, "Should have thrown an error for non-existent ship arrival");
    });
    
    it("should not allow non-owner to update a ship arrival", async () => {
      // Record the initial ship arrival
      await shipArrivalContract.recordShipArrival(
        testData.numeroAvis,
        testData.numeroEscale,
        testData.nomPort,
        testData.codePort,
        testData.nomNavire,
        testData.etat,
        { from: owner }
      );
      
      // Try to update the ship arrival as a non-owner
      let errorThrown = false;
      try {
        await shipArrivalContract.updateShipArrival(
          testData.numeroAvis,
          "ESC002",
          "Tanger",
          "TNG",
          "Another Ship",
          "EN_ATTENTE",
          { from: user }
        );
      } catch (error) {
        errorThrown = true;
        // The exact error message might vary, so we just check that an error was thrown
        assert.isTrue(error.message.includes("revert"), "Should throw a revert error");
      }
      assert.isTrue(errorThrown, "Should have thrown an error for non-owner");
    });
  });
  
  describe("Ship Events Retrieval", () => {
    it("should get ship events by ship ID", async () => {
      // Record a ship arrival
      const tx = await shipArrivalContract.recordShipArrival(
        testData.numeroAvis,
        testData.numeroEscale,
        testData.nomPort,
        testData.codePort,
        testData.nomNavire,
        testData.etat,
        { from: owner }
      );
      
      // Get the event ID from the transaction
      const event = tx.logs.find(log => log.event === "EventRecorded");
      const eventId = event.args.eventId;
      
      // Get ship events
      const shipEvents = await shipArrivalContract.getShipEvents(testData.numeroEscale);
      assert.equal(shipEvents.length, 1, "Should have one ship event");
      assert.equal(shipEvents[0], eventId, "Event ID should match");
    });
    
    it("should get port events by port ID", async () => {
      // Record a ship arrival
      const tx = await shipArrivalContract.recordShipArrival(
        testData.numeroAvis,
        testData.numeroEscale,
        testData.nomPort,
        testData.codePort,
        testData.nomNavire,
        testData.etat,
        { from: owner }
      );
      
      // Get the event ID from the transaction
      const event = tx.logs.find(log => log.event === "EventRecorded");
      const eventId = event.args.eventId;
      
      // Get port events
      const portEvents = await shipArrivalContract.getPortEvents(testData.codePort);
      assert.equal(portEvents.length, 1, "Should have one port event");
      assert.equal(portEvents[0], eventId, "Event ID should match");
    });
  });
  
  describe("Ship Arrival Existence Check", () => {
    it("should check if a ship arrival exists", async () => {
      // Record a ship arrival
      await shipArrivalContract.recordShipArrival(
        testData.numeroAvis,
        testData.numeroEscale,
        testData.nomPort,
        testData.codePort,
        testData.nomNavire,
        testData.etat,
        { from: owner }
      );
      
      // Check if the ship arrival exists
      const exists = await shipArrivalContract.shipArrivalExists(testData.numeroAvis);
      assert.isTrue(exists, "Ship arrival should exist");
      
      // Check if a non-existent ship arrival exists
      const nonExistent = await shipArrivalContract.shipArrivalExists(99999);
      assert.isFalse(nonExistent, "Non-existent ship arrival should not exist");
    });
  });
}); 