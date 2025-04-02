def gcd(a, b):
    if b == 0:
        print(f"НОД({a}, {b}) = {a}")
        return a
    else:
        print(f"НОД({a}, {b})")
        return gcd(b, a % b)

def main():
  gcd(2784, 246)

if __name__ == "__main__":
    main()
