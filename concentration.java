import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

// represents a 
class ConcentrationGame extends World {

  // represents the game card deck 
  ArrayList<Card> deck;

  // random 
  Random rand = new Random();

  // keeps track of two flipped cards at a time
  ArrayList<Card> flippedCards = new ArrayList<>();

  // keeps track of total flipped cards that are a pair
  ArrayList<Card> cardPairs = new ArrayList<>();

  // keeps track of the total pairs in the deck left, starts with 0 pairs flipped
  int totalPairsLeft = 26;

  // choose if cards must be matched by same color suit, or just by value
  boolean sameSuit;

  // boolean for if the game is over
  boolean gameOver;

  // tracks the total seconds so far in the game
  int time;

  // input the total steps you are allowed to make before the game ends
  int totalStepsAllowed;

  // tracks the amount of steps the player has made so far
  int stepsSoFar;

  // represents a concentration game board that takes in a boolean and an int
  ConcentrationGame(boolean matchBySuit, int totalStepsAllowed) {

    this.sameSuit = matchBySuit;
    this.totalStepsAllowed = totalStepsAllowed;
    this.deck = assignCards(); // starts with a random shuffled deck each time

  }

  // randomly creates an order of cards using a random

  public ArrayList<Card> assignCards() {

    ArrayList<Card> originalDeck = new Card("", "").makeCards(); 
    ArrayList<Card> shuffledDeck = new ArrayList<>();

    // while og deck is greater than size 0
    while (originalDeck.size() > 0) {
      // using random int to assign deck to a random index
      int randomIndex = rand.nextInt(originalDeck.size());

      // add to the shuffled deck while removing from the og deck
      shuffledDeck.add(originalDeck.remove(randomIndex));
    }
    return shuffledDeck;
  }

  // on tick method
  public void onTick() {
    // EFFECT: adds one to the total time on each tick if game is not over
    if (!gameOver) {
      time = time + 1;
    }
  }

  // handles mouse events
  public void onMouseClicked(Posn pos) {

    int rows = 4;
    int cols = 13;

    // card image details
    int cardWidth = 70;
    int cardHeight = 90;
    int spacingX = 10; 
    int spacingY = 10;

    // only process a click if fewer than 2 cards are flipped and game is not over
    if (!gameOver) {
      if (flippedCards.size() < 2) {
        for (int i = 0; i < rows; i++) {
          for (int j = 0; j < cols; j++) {
            int index = i * cols + j;

            if (index < deck.size()) {
              int cardX = j * (cardWidth + spacingX) + cardWidth / 2 + spacingX;
              int cardY = i * (cardHeight + spacingY) + cardHeight / 2 + spacingY;
              Card card = deck.get(index);

              // check if the card is clicked based on mouse position
              if (pos.x >= cardX - cardWidth / 2 && pos.x <= cardX + cardWidth / 2 
                  && pos.y >= cardY - cardHeight / 2 && pos.y <= cardY + cardHeight / 2) {

                // if the card is not face up, flip it
                if (!card.faceUp) {
                  card.flipFace(); 
                  flippedCards.add(card); // add the card to the list of flipped cards

                  // only once two cards are flipped, check for a pair
                  if (flippedCards.size() == 2) {
                    checkForPair();
                    // increase step counter by 1
                    stepsSoFar = stepsSoFar + 1;
                  }
                }
              }
            }
          }
        }

      } else if 
      (flippedCards.size() == 2 
      && !cardPairs.contains(flippedCards.get(0))) {

        // if two cards were flipped but they are not a pair, flip them back down
        for (Card flippedCard : flippedCards) {
          flippedCard.flipFace(); 
        }
        // clear current flipped cards list
        flippedCards.clear(); 
      }
    }
  }


  // checks if two cards are valid pairs, if they are then adds the pair
  // to the tracked total pairs, updates counter, clears flipped card list
  public void checkForPair() {

    Card firstCard = flippedCards.get(0);
    Card secondCard = flippedCards.get(1);

    // Check if the two flipped cards form a pair
    if (firstCard.equalCards(secondCard, sameSuit)) {

      // add both cards to pairs list 
      cardPairs.add(firstCard);
      cardPairs.add(secondCard);

      // sets the two pair of cards to be a correct pair
      firstCard.isPair = true;
      secondCard.isPair = true;

      // update pair counter
      updatePairs(); 
      // clear from the flipped card list
      flippedCards.clear(); 

    }
  }

  // updates the total number of pairs in the game 
  public void updatePairs() {
    // decrease total pairs left by 1
    totalPairsLeft = totalPairsLeft - 1;

  }


  // resets game board 
  public void onKeyEvent(String key) {
    if (key.equals("r") ) {
      // sets gameOver back to false
      gameOver = false;
      // make a new random deck 
      this.deck = assignCards();
      // set card pairs to empty
      cardPairs = new ArrayList<>();
      // set flipped cards to empty 
      flippedCards = new ArrayList<>();
      // set pairs left back to 26
      totalPairsLeft = 26;
      // set time to 0 
      time = 0;
      // set steps so far to 0
      stepsSoFar = 0;
    } 
  }


  // creates the initial game scene of 4 rows of cards all flipped down

  public WorldScene makeScene() {
    WorldScene scene = new WorldScene(1070, 600); 
    int cardWidth = 70;
    int cardHeight = 90;
    int spacingX = 10; 
    int spacingY = 10; 
    int rows = 4;
    int cols = 13;

    Color purple = new Color(170, 112, 245);

    // text image of time taken
    TextImage timeText = new TextImage("Time So Far: " + Integer.toString(time),
        30, Color.BLACK);

    // text image of pairs left
    TextImage pairsLeft = new TextImage("Pairs Left: " + Integer.toString(totalPairsLeft),
        30, Color.BLACK);
    TextImage gameTitle = new TextImage("concentration game!", 50, FontStyle.BOLD_ITALIC, 
        Color.PINK);
    scene.placeImageXY(pairsLeft, 535, 550);

    // ending game messages 
    TextImage youWin = new TextImage("you won :) !!!", 100, FontStyle.BOLD_ITALIC, purple);
    TextImage youLost = new TextImage("you lost :(", 100, FontStyle.BOLD_ITALIC, purple);

    // steps taken
    TextImage stepsTaken = new TextImage("steps taken: " + stepsSoFar,
        20, Color.BLACK);

    // steps left
    TextImage stepsLeft = new TextImage("steps left: " + (totalStepsAllowed - stepsSoFar),
        20, Color.BLACK);

    // place game title
    scene.placeImageXY(gameTitle, 535, 450);
    // place pairs left
    scene.placeImageXY(pairsLeft, 535, 550);
    // place time so far
    scene.placeImageXY(timeText, 535, 500);
    // place steps taken so far
    scene.placeImageXY(stepsTaken, 300, 500);
    // place steps left
    scene.placeImageXY(stepsLeft, 300, 550);

    // game is over when total pairs left is 0 or when all steps allowed have been taken
    if (totalPairsLeft == 0 || totalStepsAllowed == stepsSoFar) {
      gameOver = true;
    }

    // goes through the list of cards deck and draws them
    for (int i = 0; i < rows; i = i + 1) {
      for (int j = 0; j < cols; j = j + 1) {

        int index = i * cols + j;

        if (index < deck.size()) {
          Card card = deck.get(index);
          WorldImage cardImage = card.drawCard();

          int x = j * (cardWidth + spacingX) + cardWidth / 2 + spacingX;
          int y = i * (cardHeight + spacingY) + cardHeight / 2 + spacingY;

          scene.placeImageXY(cardImage, x, y);
        }
      }
    }

    // when game ends, display game lost or game won message
    if (gameOver) {
      if (totalPairsLeft == 0) {
        scene.placeImageXY(youWin, 535, 250);
      } else {
        scene.placeImageXY(youLost, 535, 250);
      }
    }
    return scene;
  }
}

// represents a card
class Card {

  // represents the card's suit
  String suit;
  // represents the card's value
  String value;
  // tracks if the card is face up or face down 
  Boolean faceUp;
  // tracks if the card is a correct pair
  Boolean isPair;

  Card(String suit, String value) {
    this.suit = suit;
    this.value = value;
    this.faceUp = false; // creates the cards face down initially
    this.isPair = false;
  }

  // creates a deck of cards
  public ArrayList<Card> makeCards() {
    ArrayList<String> suits = new ArrayList<String>(Arrays.asList("♥",
        "♦",  "♠", "♣"));
    ArrayList<String> values = new ArrayList<String>(Arrays.asList("A",
        "2", "3", "4",
        "5", "6", "7", "8", "9", "10", "J", "Q",
        "K"));
    ArrayList<Card> cards = new ArrayList<Card>();
    for (int i = 0; i < suits.size(); i = i + 1) {
      for (int j = 0; j < values.size(); j = j + 1) {
        cards.add(new Card(suits.get(i), values.get(j)));
      }
    }
    return cards;
  }

  // flips the face of the card 
  void flipFace() {
    // EFFECT: sets the faceUp boolean to be the opposite 
    faceUp = !faceUp;
  }


  // checks if two cards are a valid pair depending on sameSuit
  public boolean equalCards(Card card2, boolean sameSuit) {
    // sameSuit is true
    if (sameSuit) {
      // hearts and diamonds are a pair
      // clubs and spades are a pair 
      return 
          ((this.suit.equals("♥") && card2.suit.equals("♥")) 
              || (this.suit.equals("♦") && card2.suit.equals("♥")) 
              || (this.suit.equals("♠") && card2.suit.equals("♣")) 
              || (this.suit.equals("♣") && card2.suit.equals("♠")))
          && this.value.equals(card2.value);
    } else {
      // just check if the values are the same
      return 
          this.value.equals(card2.value);
    }
  }

  // draws the individual card face down or face up 
  WorldImage drawCard() {

    int cardWidth = 70;
    int cardHeight = 90;

    // draws a blank card 
    WorldImage blankCard = new RectangleImage(cardWidth, 
        cardHeight, OutlineMode.OUTLINE, Color.BLACK);
    WorldImage downCard = new RectangleImage(cardWidth, cardHeight, OutlineMode.SOLID, Color.PINK);
    // draws the suit and value in black 
    WorldImage infoBlack = new TextImage(this.value + "  " + this.suit, 25, Color.BLACK);
    // draws the suit and value in red 
    WorldImage infoRed = new TextImage(this.value + "  " + this.suit, 25, Color.RED);

    // draws a card that is a correct pair
    WorldImage pairCard = new RectangleImage(cardWidth, cardHeight, 
        OutlineMode.OUTLINE, Color.pink);

    // draw card that is a correct pair
    if (isPair) {
      if (this.suit.equals("♥") || this.suit.equals("♦")) {
        return new OverlayImage(infoRed, pairCard); }
      else { 
        // spades and clubs are black
        return new OverlayImage(infoBlack, pairCard);
      }
    }
    // draw face-up cards

    if (faceUp) {
      // hearts and diamonds are red 
      if (this.suit.equals("♥") || this.suit.equals("♦")) {
        return new OverlayImage(infoRed, blankCard); }
      else { 
        // spades and clubs are black
        return new OverlayImage(infoBlack, blankCard);
      }
    } else {
      // draw face down card
      return downCard;
    }
  }
}

class ExamplesConcentrationGame {

  // Helper method to create a sample card
  Card createSampleCard(String suit, String value) {
    return new Card(suit, value);
  }

  // Test for `makeCards` in the `Card` class
  void testMakeCards(Tester t) {
    Card sample = new Card("", "");
    ArrayList<Card> cards = sample.makeCards();
    t.checkExpect(cards.size(), 52); 
    t.checkExpect(cards.get(0), new Card("♥", "A")); 
    t.checkExpect(cards.get(51), new Card("♣", "K")); 
  }

  // Test for `flipFace` in the `Card` class
  void testFlipFace(Tester t) {
    Card card = createSampleCard("♥", "A");
    t.checkExpect(card.faceUp, false);
    card.flipFace();
    t.checkExpect(card.faceUp, true);
    card.flipFace();
    t.checkExpect(card.faceUp, false);
  }

  // Test for `equalCards` in the `Card` class
  void testEqualCards(Tester t) {
    Card card1 = createSampleCard("♥", "A");
    Card card2 = createSampleCard("♦", "A");
    Card card3 = createSampleCard("♣", "A");
    Card card4 = createSampleCard("♠", "A");
    Card card5 = createSampleCard("♥", "K");

    // Test with sameSuit = true
    t.checkExpect(card1.equalCards(card2, true), false); 
    t.checkExpect(card1.equalCards(card5, true), false); 
    t.checkExpect(card3.equalCards(card4, true), true); 

    // Test with sameSuit = false
    t.checkExpect(card1.equalCards(card3, false), true); 
    t.checkExpect(card1.equalCards(card2, false), true); 
  }

  // Test for `assignCards` in `ConcentrationGame`
  void testAssignCards(Tester t) {
    ConcentrationGame game = new ConcentrationGame(false, 10);
    ArrayList<Card> assignedDeck = game.assignCards();
    t.checkExpect(assignedDeck.size(), 52);
    t.checkExpect(new Card("", "").makeCards().containsAll(assignedDeck), false);
  }

  // Test for `onTick` in `ConcentrationGame`
  void testOnTick(Tester t) {
    ConcentrationGame game = new ConcentrationGame(false, 10);
    t.checkExpect(game.time, 0);
    game.onTick();
    t.checkExpect(game.time, 1);
    game.gameOver = true;
    game.onTick();
    t.checkExpect(game.time, 1); 
  }

  // Test for `onMouseClicked` in `ConcentrationGame`
  void testOnMouseClicked(Tester t) {
    ConcentrationGame game = new ConcentrationGame(false, 10);
    Posn pos = new Posn(50, 50); 
    Card clickedCard = game.deck.get(0);
    clickedCard.faceUp = false;

    game.onMouseClicked(pos);
    t.checkExpect(clickedCard.faceUp, true);
    t.checkExpect(game.flippedCards.size(), 1);
  }

  // Test for `checkForPair` in `ConcentrationGame`
  void testCheckForPair(Tester t) {
    ConcentrationGame game = new ConcentrationGame(true, 10);
    Card card1 = createSampleCard("♥", "A");
    Card card2 = createSampleCard("♦", "A");
    game.flippedCards.add(card1);
    game.flippedCards.add(card2);

    game.checkForPair();
    t.checkExpect(game.cardPairs.size(), 0);
    t.checkExpect(game.totalPairsLeft, 26);
  }

  // Test for `updatePairs` in `ConcentrationGame`
  void testUpdatePairs(Tester t) {
    ConcentrationGame game = new ConcentrationGame(false, 10);
    game.updatePairs();
    t.checkExpect(game.totalPairsLeft, 25);
  }

  // Test for `onKeyEvent` in `ConcentrationGame`
  void testOnKeyEvent(Tester t) {
    ConcentrationGame game = new ConcentrationGame(false, 10);
    game.totalPairsLeft = 5;
    game.onKeyEvent("r");
    t.checkExpect(game.totalPairsLeft, 26);
    t.checkExpect(game.cardPairs.size(), 0);
    t.checkExpect(game.time, 0);
  }

  // Test for `makeScene` in `ConcentrationGame`
  void testMakeScene(Tester t) {
    ConcentrationGame game = new ConcentrationGame(false, 10);
    WorldScene scene = game.makeScene();
    t.checkExpect(scene.width, 1070);
    t.checkExpect(scene.height, 600);
  }
}
