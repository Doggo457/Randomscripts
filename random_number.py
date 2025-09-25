import random

def main():
    while True:
        random_number = random.randint(1, 10)
        print("I'm thinking of a number between 1 and 100...")
        print("You have 10 attempts to guess it!")
        attempt = 0

        while attempt < 10:
            try:
                guess = int(input("Enter your guess: "))
                attempt += 1
                if guess == random_number:
                    print(f"Congratulations! You guessed it! The number was {random_number}")
                    print(f"Number of attempts = {attempt}")
                    input("Press Enter to exit...")
                    return
                elif guess < random_number:
                    print(f"Too low! Try again. ({10 - attempt} attempts remaining)")
                else:
                    print(f"Too high! Try again. ({10 - attempt} attempts remaining)")

            except ValueError:
                print("Please enter a valid number.")

        print(f"Game over! The number was {random_number}")
        play_again = input("Would you like to play again? (y/n): ").lower()
        if play_again != 'y':
            break

if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        print("\nGoodbye!")