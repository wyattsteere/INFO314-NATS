from pynats import NATSClient
import sys

# Subscribe
def callback(msg):
    print("Received a message with subject: " + msg.subject)

def main(natsurl):
    with NATSClient(url=natsurl) as nc:
        nc.connect()

        nc.subscribe(subject="*", callback=callback)

        nc.wait(count = 1)

    input("Press Enter to terminate")


natsurl = "nats://localhost:4222"
print("Starting Python message producer....")
main(natsurl)
